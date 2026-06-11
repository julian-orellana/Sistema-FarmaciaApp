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
    private final FarmaciaRepository farmaciaRepository;


    public VentaServiceImpl(
            VentaRepository ventaRepository,
            SucursalRepository sucursalRepository,
            UsuarioRepository usuarioRepository,
            ProductoRepository productoRepository,
            InventarioLotesRepository loteRepository,
            FarmaciaRepository farmaciaRepository) {
        this.ventaRepository = ventaRepository;
        this.sucursalRepository = sucursalRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
        this.farmaciaRepository = farmaciaRepository;
    }

    @Override
    public VentaResponseDTO crear(Long farmaciaId, VentaCreateDTO dto) {
        Sucursal sucursal = buscarSucursal(farmaciaId, dto.getSucursalId());
        Usuario usuario = buscarUsuario(farmaciaId, dto.getUsuarioId());
        Farmacia farmacia = farmaciaRepository.getReferenceById(farmaciaId);

        // Crear cabecera de venta
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

        // Procesar Detalles y Actualizar Inventario
        // Orden determinista por loteId: evita deadlocks entre ventas concurrentes
        // que bloquean los mismos lotes en distinto orden
        List<VentaDetalleCreateDTO> detallesOrdenados = dto.getDetalles().stream()
                .sorted(Comparator.comparing(VentaDetalleCreateDTO::getLoteId))
                .toList();

        for (VentaDetalleCreateDTO detalleDto : detallesOrdenados) {
            Producto producto = buscarProducto(farmaciaId, detalleDto.getProductoId());
            InventarioLotes lote = buscarLote(farmaciaId, detalleDto.getLoteId());

            // El lote debe pertenecer al producto indicado: si no, se estaria
            // descontando stock del lote de un producto y cobrando el precio de
            // otro (fraude de precios / corrupcion de inventario) (A1).
            if (lote.getProducto() == null ||
                    !lote.getProducto().getProductoId().equals(producto.getProductoId())) {
                throw new BadRequestException(
                        "El lote " + lote.getLoteNumero() + " no pertenece al producto indicado");
            }

            // El precio lo dicta el servidor, nunca el cliente
            BigDecimal precioUnitario = producto.getProductoPrecioVenta();

            // Validación de stock
            if (lote.getLoteCantidadActual() < detalleDto.getCantidad()) {
                throw new BadRequestException("Stock insuficiente en el lote: " + lote.getLoteNumero());
            }

            // Descuento de inventario
            lote.setLoteCantidadActual(lote.getLoteCantidadActual() - detalleDto.getCantidad());
            loteRepository.save(lote);

            // Cálculos monetarios
            BigDecimal subtotalLinea = precioUnitario.multiply(BigDecimal.valueOf(detalleDto.getCantidad()));
            acumuladorSubtotal = acumuladorSubtotal.add(subtotalLinea);

            // Crear el objeto detalle
            VentaDetalle detalle = VentaDetalle.builder()
                    .producto(producto)
                    .lote(lote)
                    .venta(venta)
                    .detalleCantidad(detalleDto.getCantidad())
                    .detallePrecioUnitario(precioUnitario)
                    .detalleSubtotal(subtotalLinea)
                    .build();

            venta.getDetalles().add(detalle);
        }

        // Totales Finales
        venta.setVentaSubtotal(acumuladorSubtotal);
        BigDecimal descuento = dto.getDescuento() != null ? dto.getDescuento() : BigDecimal.ZERO;

        // Solo un administrador puede aplicar descuentos
        if (descuento.compareTo(BigDecimal.ZERO) > 0) {
            Usuario solicitante = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (solicitante.getUsuarioRol() != UsuarioRol.administrador) {
                throw new BadRequestException("Solo un administrador puede aplicar descuentos");
            }
        }

        venta.setVentaDescuento(descuento);
        venta.setVentaTotal(acumuladorSubtotal.subtract(descuento));

        // Guardar (Se guarda cabecera y detalles por el CascadeType.ALL)
        Venta guardada = ventaRepository.save(venta);
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
    public Page<VentaSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable){
        return ventaRepository.buscarPorTexto(farmaciaId, texto, pageable)
                .map(VentaSimpleDTO::fromEntity);
    }

    @Override
    public VentaResponseDTO actualizar(Long farmaciaId, Long id, VentaUpdateDTO dto) {
        Venta venta = ventaRepository.findByVentaIdAndSucursal_Farmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada ID: " + id));

        // Actualizar datos como el nombre del cliente o NIT si hubo error
        if (dto.getNombreCliente() != null) venta.setVentaNombreCliente(dto.getNombreCliente());
        if (dto.getNitCliente() != null) venta.setVentaNitCliente(dto.getNitCliente());

        Venta guardar = ventaRepository.save(venta);
        return VentaResponseDTO.fromEntity(guardar);
    }

    @Override
    public void cambiarEstado(Long farmaciaId, Long id, VentaEstado nuevoEstado) {
        Venta venta = ventaRepository.findByVentaIdAndSucursal_Farmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada ID: " + id));

        // Una venta anulada es definitiva: no puede reactivarse a completada
        // (reactivar sin re-descontar stock corrompería el inventario)
        if (venta.getVentaEstado() == VentaEstado.anulada && nuevoEstado == VentaEstado.completada) {
            throw new BadRequestException("Una venta anulada no puede volver a completarse");
        }

        // ANULAR una venta que estaba COMPLETADA
        if (nuevoEstado == VentaEstado.anulada && venta.getVentaEstado() == VentaEstado.completada) {
            for (VentaDetalle detalle : venta.getDetalles()) {
                InventarioLotes lote = detalle.getLote();

                // Devolvemos el stock al lote
                lote.setLoteCantidadActual(lote.getLoteCantidadActual() + detalle.getDetalleCantidad());

                // Si el lote estaba agotado, ahora tiene stock, así que vuelve a estar disponible
                if (lote.getLoteEstado() == LoteEstado.agotado) {
                    lote.setLoteEstado(LoteEstado.disponible);
                }

                loteRepository.save(lote);
            }
        }

        // Actualizamos el estado de la venta
        venta.setVentaEstado(nuevoEstado);
        ventaRepository.save(venta);
    }

    @Override
    public void eliminar(Long farmaciaId, Long id) {
        cambiarEstado(farmaciaId, id, VentaEstado.anulada);
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

    // Carga con lock pesimista: la fila queda bloqueada hasta el commit de la venta
    private InventarioLotes buscarLote(Long farmaciaId, Long id) {
        return loteRepository.findByLoteIdAndFarmaciaIdForUpdate(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado en tu farmacia ID: " + id));
    }
}