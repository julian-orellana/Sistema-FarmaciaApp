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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

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

    public CompraServiceImpl(
            CompraRepository compraRepository,
            SucursalRepository sucursalRepository,
            UsuarioRepository usuarioRepository,
            ProductoRepository productoRepository,
            InventarioLotesRepository loteRepository,
            InventarioRepository inventarioRepository,
            FarmaciaRepository farmaciaRepository) {
        this.compraRepository = compraRepository;
        this.sucursalRepository = sucursalRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
        this.inventarioRepository = inventarioRepository;
        this.farmaciaRepository = farmaciaRepository;
    }

    @Override
    public CompraResponseDTO crear(Long farmaciaId, CompraCreateDTO dto) {
        Sucursal sucursal = buscarSucursal(farmaciaId, dto.getSucursalId());

        // Quien registra la compra es quien esta autenticado, nunca un id del request (M4)
        Usuario solicitante = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = buscarUsuario(farmaciaId, solicitante.getUsuarioId());
        Farmacia farmacia = farmaciaRepository.getReferenceById(farmaciaId);

        // 1. Crear Cabecera
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

        // 2. Procesar Detalles y Lotes
        for (CompraDetalleCreateDTO detDto : dto.getDetalles()) {
            Producto producto = buscarProducto(farmaciaId, detDto.getProductoId());

            // Buscar lote existente (scopeado a sucursal+producto) o crear uno nuevo.
            // Sin el scope, findByLoteNumero global podia sumar stock al lote de otra
            // farmacia que compartiera el mismo numero de lote (A2).
            InventarioLotes lote = loteRepository
                    .findByLoteNumeroAndSucursal_SucursalIdAndProducto_ProductoId(
                            detDto.getNumeroLote(), sucursal.getSucursalId(), producto.getProductoId())
                    .orElseGet(() -> InventarioLotes.builder()
                            .loteNumero(detDto.getNumeroLote())
                            .loteFechaVencimiento(detDto.getFechaVencimiento())
                            .loteCantidadActual(0)
                            .loteEstado(LoteEstado.disponible)
                            .producto(producto)
                            .sucursal(sucursal)
                            .farmacia(farmacia)
                            .build());

            // Aumentar stock del lote
            lote.setLoteCantidadActual(lote.getLoteCantidadActual() + detDto.getCantidad());
            loteRepository.save(lote);

            // Mantener sincronizado el inventario agregado (producto+sucursal),
            // creándolo si la compra es el primer ingreso de stock de ese par (M9)
            ajustarInventarioAgregado(farmacia, producto, sucursal, detDto.getCantidad());

            // Cálculos
            BigDecimal subtotal = detDto.getPrecioUnitario().multiply(BigDecimal.valueOf(detDto.getCantidad()));
            totalAcumulado = totalAcumulado.add(subtotal);

            // Crear detalle
            CompraDetalle detalle = CompraDetalle.builder()
                    .compra(compra)
                    .producto(producto)
                    .loteId(lote)
                    .detalleCantidad(detDto.getCantidad())
                    .detallePrecioUnitario(detDto.getPrecioUnitario())
                    .detalleSubtotal(subtotal)
                    .build();

            compra.getDetalles().add(detalle);
        }

        compra.setCompraTotal(totalAcumulado);
        Compra guardada = compraRepository.save(compra);
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
    public Page<CompraSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable){
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

        // Revertir stock si se anula
        if (nuevoEstado == CompraEstado.anulada && compra.getCompraEstado() == CompraEstado.activa) {
            for (CompraDetalle detalle : compra.getDetalles()) {
                // Lock pesimista: evita que una venta concurrente descuente el lote
                // entre el check y el commit de la anulacion (M5)
                InventarioLotes lote = loteRepository
                        .findByLoteIdAndFarmaciaIdForUpdate(detalle.getLoteId().getLoteId(), farmaciaId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Lote no encontrado en tu farmacia ID: " + detalle.getLoteId().getLoteId()));

                // Si parte de lo comprado ya se vendio, revertir dejaria stock negativo (M5)
                if (lote.getLoteCantidadActual() < detalle.getDetalleCantidad()) {
                    throw new BadRequestException(
                            "No se puede anular la compra: el lote " + lote.getLoteNumero()
                                    + " ya tiene unidades vendidas (stock actual "
                                    + lote.getLoteCantidadActual() + ", se necesitan "
                                    + detalle.getDetalleCantidad() + ")");
                }

                lote.setLoteCantidadActual(lote.getLoteCantidadActual() - detalle.getDetalleCantidad());
                loteRepository.save(lote);

                // Revertir también la cantidad del inventario agregado (M9).
                // El inventario ya existe (la compra original lo creó/incrementó), así
                // que la farmacia solo se usaría al crear: la tomamos del lote, que
                // siempre la tiene seteada.
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

    // Métodos auxiliares privados
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

    // Aplica un delta sobre el inventario agregado producto+sucursal, con lock
    // pesimista. Si la compra es el primer ingreso de stock de ese par, no existe
    // todavía la fila Inventario y se crea (cantidad mínima por defecto 0). Se
    // bloquea después del lote para conservar un orden de locks consistente (M9).
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