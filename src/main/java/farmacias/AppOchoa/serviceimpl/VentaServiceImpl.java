package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.venta.VentaCreateDTO;
import farmacias.AppOchoa.dto.venta.VentaResponseDTO;
import farmacias.AppOchoa.dto.venta.VentaSimpleDTO;
import farmacias.AppOchoa.dto.venta.VentaUpdateDTO;
import farmacias.AppOchoa.dto.ventadetalle.VentaDetalleCreateDTO;
import farmacias.AppOchoa.model.*;
import farmacias.AppOchoa.repository.*;
import farmacias.AppOchoa.exception.BadRequestException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.services.KardexService;
import farmacias.AppOchoa.services.VentaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final InventarioLotesRepository loteRepository;
    private final InventarioRepository inventarioRepository;
    private final FarmaciaRepository farmaciaRepository;
    private final KardexService kardexService;

    public VentaServiceImpl(
            VentaRepository ventaRepository,
            SucursalRepository sucursalRepository,
            UsuarioRepository usuarioRepository,
            ProductoRepository productoRepository,
            InventarioLotesRepository loteRepository,
            InventarioRepository inventarioRepository,
            FarmaciaRepository farmaciaRepository,
            KardexService kardexService) {
        this.ventaRepository = ventaRepository;
        this.sucursalRepository = sucursalRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
        this.inventarioRepository = inventarioRepository;
        this.farmaciaRepository = farmaciaRepository;
        this.kardexService = kardexService;
    }

    @Override
    public VentaResponseDTO crear(Long farmaciaId, VentaCreateDTO dto) {
        Sucursal sucursal = buscarSucursal(farmaciaId, dto.getSucursalId());

        // El cajero se extrae del contexto de seguridad para evitar
        // que el cliente pueda suplantar otro usuario en el request body
        Usuario solicitante = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = buscarUsuario(farmaciaId, solicitante.getUsuarioId());
        Farmacia farmacia = farmaciaRepository.getReferenceById(farmaciaId);

        Venta venta = Venta.builder()
                .sucursal(sucursal)
                .usuario(usuario)
                .ventaNitCliente(dto.getNitCliente() != null ? dto.getNitCliente() : "CF")
                .ventaNombreCliente(dto.getNombreCliente() != null ? dto.getNombreCliente() : "Consumidor Final")
                .ventaEstado(VentaEstado.completada)
                .detalles(new ArrayList<>())
                .farmacia(farmacia)
                .build();

        BigDecimal acumuladorSubtotal = BigDecimal.ZERO;

        // Orden determinista por loteId para prevenir deadlocks entre ventas
        // concurrentes que bloqueen los mismos lotes en distinto orden
        List<VentaDetalleCreateDTO> detallesOrdenados = dto.getDetalles().stream()
                .sorted(Comparator.comparing(VentaDetalleCreateDTO::getLoteId))
                .toList();

        for (VentaDetalleCreateDTO detalleDto : detallesOrdenados) {
            Producto producto = buscarProducto(farmaciaId, detalleDto.getProductoId());
            InventarioLotes lote = buscarLote(farmaciaId, detalleDto.getLoteId());

            // Validar que el lote pertenece al producto indicado; de lo contrario
            // se estaría descontando stock de un producto y cobrando el precio de otro
            if (lote.getProducto() == null ||
                    !lote.getProducto().getProductoId().equals(producto.getProductoId())) {
                throw new BadRequestException(
                        "El lote " + lote.getLoteNumero() + " no pertenece al producto indicado");
            }

            // El precio de venta lo dicta el servidor, nunca el cliente
            BigDecimal precioUnitario = producto.getProductoPrecioVenta();

            if (lote.getLoteCantidadActual() < detalleDto.getCantidad()) {
                throw new BadRequestException("Stock insuficiente en el lote: " + lote.getLoteNumero());
            }

            lote.setLoteCantidadActual(lote.getLoteCantidadActual() - detalleDto.getCantidad());
            loteRepository.save(lote);

            // Sincroniza el inventario agregado producto+sucursal;
            // se bloquea después del lote para mantener orden de locks consistente
            ajustarInventarioAgregado(producto.getProductoId(), sucursal.getSucursalId(),
                    -detalleDto.getCantidad());

            BigDecimal subtotalLinea = precioUnitario.multiply(BigDecimal.valueOf(detalleDto.getCantidad()));
            acumuladorSubtotal = acumuladorSubtotal.add(subtotalLinea);

            venta.getDetalles().add(VentaDetalle.builder()
                    .producto(producto)
                    .lote(lote)
                    .venta(venta)
                    .detalleCantidad(detalleDto.getCantidad())
                    .detallePrecioUnitario(precioUnitario)
                    .detalleSubtotal(subtotalLinea)
                    .build());
        }

        venta.setVentaSubtotal(acumuladorSubtotal);
        BigDecimal descuento = dto.getDescuento() != null ? dto.getDescuento() : BigDecimal.ZERO;

        // El rol se lee de BD vía buscarUsuario, no del token, para evitar
        // que un token desactualizado otorgue privilegios revocados
        if (descuento.compareTo(BigDecimal.ZERO) > 0
                && usuario.getUsuarioRol() != UsuarioRol.administrador) {
            throw new BadRequestException("Solo un administrador puede aplicar descuentos");
        }

        // Un descuento mayor al subtotal generaría un total negativo,
        // lo que permitiría extraer dinero de caja como vuelto
        if (descuento.compareTo(acumuladorSubtotal) > 0) {
            throw new BadRequestException("El descuento no puede superar el subtotal de la venta");
        }

        venta.setVentaDescuento(descuento);
        venta.setVentaTotal(acumuladorSubtotal.subtract(descuento));

        // Cabecera y detalles se persisten en un solo save por CascadeType.ALL
        Venta guardada = ventaRepository.save(venta);

        // Kardex post-save para disponer del ventaId como referencia del movimiento;
        // el stockResultante se recalcula desde el inventario ya actualizado
        for (VentaDetalle detalle : guardada.getDetalles()) {
            int stockResultante = inventarioRepository
                    .findByProductoYSucursalForUpdate(
                            detalle.getProducto().getProductoId(),
                            sucursal.getSucursalId())
                    .map(Inventario::getInventarioCantidadActual)
                    .orElse(0);
            int stockAnterior = stockResultante + detalle.getDetalleCantidad();

            kardexService.registrarMovimiento(
                    detalle.getProducto(),
                    farmacia,
                    usuario,
                    detalle.getDetalleCantidad(),
                    TipoMovimiento.SALIDA,
                    stockAnterior,
                    stockResultante,
                    detalle.getDetallePrecioUnitario(),
                    guardada.getVentaId(),
                    "VENTA",
                    null
            );
        }

        return VentaResponseDTO.fromEntity(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponseDTO listarPorId(Long farmaciaId, Long id) {
        Venta venta = ventaRepository.findByVentaIdAndSucursal_Farmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada ID: " + id));
        return VentaResponseDTO.fromEntity(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaSimpleDTO> listarTodasPaginadas(Long farmaciaId, Pageable pageable) {
        return ventaRepository.findByFarmacia_FarmaciaId(farmaciaId, pageable)
                .map(VentaSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaSimpleDTO> listarActivasPaginadas(Long farmaciaId, Pageable pageable) {
        return ventaRepository.findByFarmacia_FarmaciaId(farmaciaId, pageable)
                .map(VentaSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable) {
        return ventaRepository.buscarPorTexto(farmaciaId, texto, pageable)
                .map(VentaSimpleDTO::fromEntity);
    }

    @Override
    public VentaResponseDTO actualizar(Long farmaciaId, Long id, VentaUpdateDTO dto) {
        Venta venta = ventaRepository.findByVentaIdAndSucursal_Farmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada ID: " + id));

        if (dto.getNombreCliente() != null) venta.setVentaNombreCliente(dto.getNombreCliente());
        if (dto.getNitCliente() != null) venta.setVentaNitCliente(dto.getNitCliente());

        return VentaResponseDTO.fromEntity(ventaRepository.save(venta));
    }

    @Override
    public void cambiarEstado(Long farmaciaId, Long id, VentaEstado nuevoEstado) {
        Venta venta = ventaRepository.findByVentaIdAndSucursal_Farmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada ID: " + id));

        // Reactivar una venta anulada corrompería el inventario
        // porque el stock ya fue devuelto al momento de la anulación
        if (venta.getVentaEstado() == VentaEstado.anulada && nuevoEstado == VentaEstado.completada) {
            throw new BadRequestException("Una venta anulada no puede volver a completarse");
        }

        if (nuevoEstado == VentaEstado.anulada && venta.getVentaEstado() == VentaEstado.completada) {
            for (VentaDetalle detalle : venta.getDetalles()) {
                InventarioLotes lote = detalle.getLote();

                lote.setLoteCantidadActual(lote.getLoteCantidadActual() + detalle.getDetalleCantidad());

                // Si el lote estaba agotado, la devolución lo reactiva
                if (lote.getLoteEstado() == LoteEstado.agotado) {
                    lote.setLoteEstado(LoteEstado.disponible);
                }

                loteRepository.save(lote);

                // El kardex registra la devolución como ENTRADA referenciando
                // la venta original para mantener trazabilidad completa
                kardexService.registrarMovimiento(
                        lote.getProducto(),
                        lote.getFarmacia(),
                        venta.getUsuario(),
                        detalle.getDetalleCantidad(),
                        TipoMovimiento.ENTRADA,
                        lote.getLoteCantidadActual() - detalle.getDetalleCantidad(),
                        lote.getLoteCantidadActual(),
                        detalle.getDetallePrecioUnitario(),
                        venta.getVentaId(),
                        "ANULACION_VENTA",
                        null
                );

                ajustarInventarioAgregado(lote.getProducto().getProductoId(),
                        lote.getSucursal().getSucursalId(), detalle.getDetalleCantidad());
            }
        }

        venta.setVentaEstado(nuevoEstado);
        ventaRepository.save(venta);
    }

    @Override
    public void eliminar(Long farmaciaId, Long id) {
        cambiarEstado(farmaciaId, id, VentaEstado.anulada);
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

    // Lock pesimista sobre el lote: la fila queda bloqueada hasta el commit
    // para evitar que una venta concurrente descuente el mismo lote simultáneamente
    private InventarioLotes buscarLote(Long farmaciaId, Long id) {
        return loteRepository.findByLoteIdAndFarmaciaIdForUpdate(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado en tu farmacia ID: " + id));
    }

    // Aplica un delta sobre el inventario agregado producto+sucursal con lock pesimista.
    // En ventas el inventario siempre debe existir; si falta indica inconsistencia de datos
    private void ajustarInventarioAgregado(Long productoId, Long sucursalId, int delta) {
        Inventario inventario = inventarioRepository
                .findByProductoYSucursalForUpdate(productoId, sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventario inexistente para producto " + productoId
                                + " en sucursal " + sucursalId));
        inventario.setInventarioCantidadActual(inventario.getInventarioCantidadActual() + delta);
        inventarioRepository.save(inventario);
    }
}