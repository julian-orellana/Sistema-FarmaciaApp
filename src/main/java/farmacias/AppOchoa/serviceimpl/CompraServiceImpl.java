package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.compra.CompraCreateDTO;
import farmacias.AppOchoa.dto.compra.CompraResponseDTO;
import farmacias.AppOchoa.dto.compra.CompraSimpleDTO;
import farmacias.AppOchoa.dto.compra.CompraUpdateDTO;
import farmacias.AppOchoa.dto.compradetalle.CompraDetalleCreateDTO;
import farmacias.AppOchoa.model.*;
import farmacias.AppOchoa.repository.*;
import farmacias.AppOchoa.exception.BadRequestException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.services.CompraService;
import farmacias.AppOchoa.services.KardexService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CompraServiceImpl implements CompraService {

    private final CompraRepository compraRepository;
    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final InventarioLotesRepository loteRepository;
    private final InventarioRepository inventarioRepository;
    private final FarmaciaRepository farmaciaRepository;
    private final KardexService kardexService;

    public CompraServiceImpl(
            CompraRepository compraRepository,
            SucursalRepository sucursalRepository,
            UsuarioRepository usuarioRepository,
            ProductoRepository productoRepository,
            InventarioLotesRepository loteRepository,
            InventarioRepository inventarioRepository,
            FarmaciaRepository farmaciaRepository,
            KardexService kardexService) {
        this.compraRepository = compraRepository;
        this.sucursalRepository = sucursalRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
        this.inventarioRepository = inventarioRepository;
        this.farmaciaRepository = farmaciaRepository;
        this.kardexService = kardexService;
    }

    @Override
    public CompraResponseDTO crear(Long farmaciaId, CompraCreateDTO dto) {

        // El registrador se extrae del contexto de seguridad, no del request body,
        // para evitar que el cliente suplante a otro usuario enviando un usuarioId ajeno.
        Usuario solicitante = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = buscarUsuario(farmaciaId, solicitante.getUsuarioId());

        // Regla de autorización: el encargado solo registra compras de su propia
        // sucursal; los demás roles pueden elegirla en el DTO.
        Sucursal sucursal;
        if (usuario.getUsuarioRol() == UsuarioRol.encargado) {
            sucursal = usuario.getSucursal();
        } else {
            sucursal = buscarSucursal(farmaciaId, dto.getSucursalId());
        }

        Farmacia farmacia = farmaciaRepository.getReferenceById(farmaciaId);

        Compra compra = Compra.builder()
                .sucursal(sucursal)
                .usuario(usuario)
                .compraFecha(dto.getFechaCompra())
                .compraObservaciones(dto.getObservaciones())
                .compraEstado(CompraEstado.activa)
                .detalles(new ArrayList<>())
                .farmacia(farmacia)
                .build();

        BigDecimal totalAcumulado = BigDecimal.ZERO;

        // Inventario agregado gestionado, indexado por producto. Se lee de BD con lock
        // una sola vez (en la primera línea del producto) y su cantidadActual funciona
        // como stock corriente que se encadena línea a línea dentro de esta compra: así,
        // si el mismo producto entra en varios lotes, cada línea parte del saldo que dejó
        // la anterior en lugar de releer una BD ya modificada.
        Map<Long, Inventario> inventarioPorProducto = new HashMap<>();

        // Foto del movimiento de kardex POR LÍNEA de detalle (no por producto): cada
        // detalle conserva su propio stockAnterior/stockPosterior. Se difiere hasta
        // después del save porque el movimiento referencia el compraId ya generado.
        List<KardexSnapshot> snapshotsKardex = new ArrayList<>();

        for (CompraDetalleCreateDTO detDto : dto.getDetalles()) {
            Producto producto = buscarProducto(farmaciaId, detDto.getProductoId());

            // Lee el inventario (con lock pesimista) solo la primera vez que aparece el
            // producto; las líneas siguientes reutilizan la entidad gestionada en memoria.
            Inventario inventario = inventarioPorProducto.computeIfAbsent(
                    producto.getProductoId(),
                    pid -> obtenerInventarioConLock(farmacia, producto, sucursal));

            int stockAnterior = inventario.getInventarioCantidadActual();
            int stockPosterior = stockAnterior + detDto.getCantidad();
            inventario.setInventarioCantidadActual(stockPosterior);

            // El lote se busca scopeado a sucursal+producto para que dos farmacias con el
            // mismo número de lote no compartan ni contaminen stock entre sí. Si no existe
            // se crea; si existe, se le acumula la cantidad.
            InventarioLotes lote = loteRepository
                    .findByLoteNumeroAndSucursal_SucursalIdAndProducto_ProductoId(
                            detDto.getNumeroLote(), sucursal.getSucursalId(), producto.getProductoId())
                    .orElseGet(() -> InventarioLotes.builder()
                            .loteNumero(detDto.getNumeroLote())
                            .loteFechaVencimiento(detDto.getFechaVencimiento())
                            .loteCantidadInicial(detDto.getCantidad())
                            .lotePrecioCompra(detDto.getPrecioUnitario())
                            .loteCantidadActual(0)
                            .loteEstado(LoteEstado.disponible)
                            .producto(producto)
                            .sucursal(sucursal)
                            .farmacia(farmacia)
                            .build());

            lote.setLoteCantidadActual(lote.getLoteCantidadActual() + detDto.getCantidad());
            loteRepository.save(lote);

            BigDecimal subtotal = detDto.getPrecioUnitario().multiply(BigDecimal.valueOf(detDto.getCantidad()));
            totalAcumulado = totalAcumulado.add(subtotal);

            CompraDetalle detalle = CompraDetalle.builder()
                    .compra(compra)
                    .producto(producto)
                    .loteId(lote)
                    .detalleCantidad(detDto.getCantidad())
                    .detallePrecioUnitario(detDto.getPrecioUnitario())
                    .detalleSubtotal(subtotal)
                    .build();
            compra.getDetalles().add(detalle);
            snapshotsKardex.add(new KardexSnapshot(detalle, stockAnterior, stockPosterior));
        }

        // Persiste el inventario agregado una sola vez por producto (incluye los recién
        // creados); el encadenado en memoria ya dejó la cantidadActual final.
        inventarioRepository.saveAll(inventarioPorProducto.values());

        compra.setCompraTotal(totalAcumulado);
        Compra guardada = compraRepository.save(compra);

        // El kardex se registra después del save para disponer del compraId como referencia.
        for (KardexSnapshot snap : snapshotsKardex) {
            kardexService.registrarMovimiento(
                    snap.detalle().getProducto(),
                    farmacia,
                    usuario,
                    snap.detalle().getDetalleCantidad(),
                    TipoMovimiento.ENTRADA,
                    snap.stockAnterior(),
                    snap.stockPosterior(),
                    snap.detalle().getDetallePrecioUnitario(),
                    guardada.getCompraId(),
                    "COMPRA",
                    guardada.getCompraObservaciones()
            );
        }

        return CompraResponseDTO.fromEntity(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public CompraResponseDTO listarPorId(Long farmaciaId, Long id) {
        Compra compra = compraRepository.findByCompraIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada ID: " + id));
        return CompraResponseDTO.fromEntity(compra);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompraSimpleDTO> listarTodasPaginadas(Long farmaciaId, Pageable pageable) {
        return compraRepository.findByFarmacia_FarmaciaId(farmaciaId, pageable)
                .map(CompraSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompraSimpleDTO> listarActivasPaginadas(Long farmaciaId, Pageable pageable) {
        return compraRepository.findByFarmacia_FarmaciaId(farmaciaId, pageable)
                .map(CompraSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompraSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable) {
        return compraRepository.buscarPorTexto(farmaciaId, texto, pageable)
                .map(CompraSimpleDTO::fromEntity);
    }

    @Override
    public CompraResponseDTO actualizar(Long farmaciaId, Long id, CompraUpdateDTO dto) {
        Compra compra = compraRepository.findByCompraIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada ID: " + id));

        // Solo se permite editar observaciones; montos y stock son inmutables una vez
        // registrada la compra (se corrigen vía anulación).
        if (dto.getObservaciones() != null) {
            compra.setCompraObservaciones(dto.getObservaciones());
        }

        return CompraResponseDTO.fromEntity(compraRepository.save(compra));
    }

    @Override
    public void cambiarEstado(Long farmaciaId, Long id, CompraEstado nuevoEstado) {
        Compra compra = compraRepository.findByCompraIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada ID: " + id));

        // Al anular una compra activa hay que revertir el stock que ingresó.
        if (nuevoEstado == CompraEstado.anulada && compra.getCompraEstado() == CompraEstado.activa) {
            for (CompraDetalle detalle : compra.getDetalles()) {
                // Lock pesimista sobre el lote para evitar race condition entre la anulación
                // y una venta concurrente que descuente el mismo lote.
                InventarioLotes lote = loteRepository
                        .findByLoteIdAndFarmaciaIdForUpdate(detalle.getLoteId().getLoteId(), farmaciaId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Lote no encontrado en tu farmacia ID: " + detalle.getLoteId().getLoteId()));

                // Si parte del lote ya se vendió, revertir dejaría stock negativo: se rechaza.
                if (lote.getLoteCantidadActual() < detalle.getDetalleCantidad()) {
                    throw new BadRequestException(
                            "No se puede anular la compra: el lote " + lote.getLoteNumero()
                                    + " ya tiene unidades vendidas (stock actual "
                                    + lote.getLoteCantidadActual() + ", se necesitan "
                                    + detalle.getDetalleCantidad() + ")");
                }

                lote.setLoteCantidadActual(lote.getLoteCantidadActual() - detalle.getDetalleCantidad());
                loteRepository.save(lote);

                // Refleja la reversión en el inventario agregado.
                ajustarInventarioAgregado(lote.getFarmacia(),
                        lote.getProducto(), lote.getSucursal(), -detalle.getDetalleCantidad());
            }
        }

        compra.setCompraEstado(nuevoEstado);
        compraRepository.save(compra);
    }

    @Override
    public void eliminar(Long farmaciaId, Long id) {
        // Borrado lógico: una compra nunca se elimina físicamente, se anula
        // (preserva la traza contable y de kardex).
        cambiarEstado(farmaciaId, id, CompraEstado.anulada);
    }


    private Sucursal buscarSucursal(Long farmaciaId, Long id) {
        return sucursalRepository.findBySucursalIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada en tu farmacia ID: " + id));
    }

    private Usuario buscarUsuario(Long farmaciaId, Long id) {
        return usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado en tu farmacia ID: " + id));
    }

    private Producto buscarProducto(Long farmaciaId, Long id) {
        // Filtro por farmacia para impedir referenciar productos de otra (multi-tenancy).
        return productoRepository.findById(id)
                .filter(p -> p.getFarmacia().getFarmaciaId().equals(farmaciaId))
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado en tu farmacia ID: " + id));
    }

    /**
     * Obtiene el inventario agregado producto+sucursal con lock pesimista
     * (SELECT ... FOR UPDATE), o construye uno nuevo en memoria si es el primer
     * ingreso de ese par. No persiste: el llamador decide cuándo guardar.
     */
    private Inventario obtenerInventarioConLock(Farmacia farmacia, Producto producto, Sucursal sucursal) {
        return inventarioRepository
                .findByProductoYSucursalForUpdate(producto.getProductoId(), sucursal.getSucursalId())
                .orElseGet(() -> Inventario.builder()
                        .producto(producto)
                        .sucursal(sucursal)
                        .farmacia(farmacia)
                        .inventarioCantidadActual(0)
                        .inventarioCantidadMinima(0)
                        .build());
    }

    /**
     * Aplica un delta (positivo o negativo) sobre el inventario agregado
     * producto+sucursal bajo lock pesimista y lo persiste. Usado por la anulación;
     * el alta de compra encadena el stock en memoria y persiste con saveAll.
     */
    private void ajustarInventarioAgregado(Farmacia farmacia, Producto producto, Sucursal sucursal, int delta) {
        Inventario inventario = obtenerInventarioConLock(farmacia, producto, sucursal);
        inventario.setInventarioCantidadActual(inventario.getInventarioCantidadActual() + delta);
        inventarioRepository.save(inventario);
    }

    /**
     * Foto del stock de una línea de detalle para registrar su movimiento de kardex
     * tras persistir la compra. Es por línea (no por producto) para que varias líneas
     * del mismo producto conserven cada una su anterior/posterior encadenado.
     */
    private record KardexSnapshot(CompraDetalle detalle, int stockAnterior, int stockPosterior) {}
}