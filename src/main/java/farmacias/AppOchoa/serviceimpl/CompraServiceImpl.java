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

        // El registrador se extrae del contexto de seguridad para evitar
        // que el cliente pueda suplantar otro usuario en el request body
        Usuario solicitante = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = buscarUsuario(farmaciaId, solicitante.getUsuarioId());

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

        // Se captura el stock antes de cualquier modificación por detalle,
        // para registrar correctamente el stockAnterior en el kardex
        Map<Long, Integer> stockAnteriorMap = new HashMap<>();

        for (CompraDetalleCreateDTO detDto : dto.getDetalles()) {
            Producto producto = buscarProducto(farmaciaId, detDto.getProductoId());

            int stockActual = inventarioRepository
                    .findByProductoYSucursalForUpdate(producto.getProductoId(), sucursal.getSucursalId())
                    .map(Inventario::getInventarioCantidadActual)
                    .orElse(0);
            stockAnteriorMap.put(producto.getProductoId(), stockActual);

            // El lote se busca scopeado a sucursal+producto para evitar que dos farmacias
            // con el mismo número de lote compartan o contaminen stock entre sí
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

            // Sincroniza el inventario agregado producto+sucursal;
            // si es el primer ingreso de ese par, lo crea automáticamente
            ajustarInventarioAgregado(farmacia, producto, sucursal, detDto.getCantidad());

            BigDecimal subtotal = detDto.getPrecioUnitario().multiply(BigDecimal.valueOf(detDto.getCantidad()));
            totalAcumulado = totalAcumulado.add(subtotal);

            compra.getDetalles().add(CompraDetalle.builder()
                    .compra(compra)
                    .producto(producto)
                    .loteId(lote)
                    .detalleCantidad(detDto.getCantidad())
                    .detallePrecioUnitario(detDto.getPrecioUnitario())
                    .detalleSubtotal(subtotal)
                    .build());
        }

        compra.setCompraTotal(totalAcumulado);
        Compra guardada = compraRepository.save(compra);

        // El kardex se registra después del save para disponer del compraId como referencia
        for (CompraDetalle detalle : guardada.getDetalles()) {
            int stockAnterior = stockAnteriorMap.get(detalle.getProducto().getProductoId());
            kardexService.registrarMovimiento(
                    detalle.getProducto(),
                    farmacia,
                    usuario,
                    detalle.getDetalleCantidad(),
                    TipoMovimiento.ENTRADA,
                    stockAnterior,
                    stockAnterior + detalle.getDetalleCantidad(),
                    detalle.getDetallePrecioUnitario(),
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

        if (dto.getObservaciones() != null) {
            compra.setCompraObservaciones(dto.getObservaciones());
        }

        return CompraResponseDTO.fromEntity(compraRepository.save(compra));
    }

    @Override
    public void cambiarEstado(Long farmaciaId, Long id, CompraEstado nuevoEstado) {
        Compra compra = compraRepository.findByCompraIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada ID: " + id));

        if (nuevoEstado == CompraEstado.anulada && compra.getCompraEstado() == CompraEstado.activa) {
            for (CompraDetalle detalle : compra.getDetalles()) {
                // Lock pesimista para evitar race condition entre la anulación
                // y una venta concurrente que descuente el mismo lote
                InventarioLotes lote = loteRepository
                        .findByLoteIdAndFarmaciaIdForUpdate(detalle.getLoteId().getLoteId(), farmaciaId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Lote no encontrado en tu farmacia ID: " + detalle.getLoteId().getLoteId()));

                // Si el stock actual es menor a lo comprado, parte del lote ya fue vendido;
                // revertir causaría stock negativo
                if (lote.getLoteCantidadActual() < detalle.getDetalleCantidad()) {
                    throw new BadRequestException(
                            "No se puede anular la compra: el lote " + lote.getLoteNumero()
                                    + " ya tiene unidades vendidas (stock actual "
                                    + lote.getLoteCantidadActual() + ", se necesitan "
                                    + detalle.getDetalleCantidad() + ")");
                }

                lote.setLoteCantidadActual(lote.getLoteCantidadActual() - detalle.getDetalleCantidad());
                loteRepository.save(lote);

                ajustarInventarioAgregado(lote.getFarmacia(),
                        lote.getProducto(), lote.getSucursal(), -detalle.getDetalleCantidad());
            }
        }

        compra.setCompraEstado(nuevoEstado);
        compraRepository.save(compra);
    }

    @Override
    public void eliminar(Long farmaciaId, Long id) {
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
        return productoRepository.findById(id)
                .filter(p -> p.getFarmacia().getFarmaciaId().equals(farmaciaId))
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado en tu farmacia ID: " + id));
    }

    // Aplica un delta positivo o negativo sobre el inventario agregado producto+sucursal.
    // El lock pesimista se adquiere después del lote para mantener un orden de locks
    // consistente y prevenir deadlocks en operaciones concurrentes
    private void ajustarInventarioAgregado(Farmacia farmacia, Producto producto, Sucursal sucursal, int delta) {
        Inventario inventario = inventarioRepository
                .findByProductoYSucursalForUpdate(producto.getProductoId(), sucursal.getSucursalId())
                .orElseGet(() -> Inventario.builder()
                        .producto(producto)
                        .sucursal(sucursal)
                        .farmacia(farmacia)
                        .inventarioCantidadActual(0)
                        .inventarioCantidadMinima(0)
                        .build());

        inventario.setInventarioCantidadActual(inventario.getInventarioCantidadActual() + delta);
        inventarioRepository.save(inventario);
    }
}