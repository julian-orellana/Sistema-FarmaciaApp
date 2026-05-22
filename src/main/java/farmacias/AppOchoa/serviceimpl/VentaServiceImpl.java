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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@Transactional
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final InventarioLotesRepository loteRepository;

    public VentaServiceImpl(
            VentaRepository ventaRepository,
            SucursalRepository sucursalRepository,
            UsuarioRepository usuarioRepository,
            ProductoRepository productoRepository,
            InventarioLotesRepository loteRepository) {
        this.ventaRepository = ventaRepository;
        this.sucursalRepository = sucursalRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
    }

    @Override
    public VentaResponseDTO crear(Long farmaciaId, VentaCreateDTO dto) {
        Sucursal sucursal = buscarSucursal(dto.getSucursalId());
        Usuario usuario = buscarUsuario(dto.getUsuarioId());

        // Crear cabecera de venta
        Venta venta = Venta.builder()
                .sucursal(sucursal)
                .usuario(usuario)
                .ventaNitCliente(dto.getNitCliente() != null ? dto.getNitCliente() : "CF")
                .ventaNombreCliente(dto.getNombreCliente() != null ? dto.getNombreCliente() : "Consumidor Final")
                .ventaEstado(VentaEstado.completada)
                .detalles(new ArrayList<>())
                .build();

        BigDecimal acumuladorSubtotal = BigDecimal.ZERO;

        // Procesar Detalles y Actualizar Inventario
        for (VentaDetalleCreateDTO detalleDto : dto.getDetalles()) {
            Producto producto = buscarProducto(detalleDto.getProductoId());
            InventarioLotes lote = buscarLote(detalleDto.getLoteId());

            // Validación de stock
            if (lote.getLoteCantidadActual() < detalleDto.getCantidad()) {
                throw new BadRequestException("Stock insuficiente en el lote: " + lote.getLoteNumero());
            }

            // Descuento de inventario
            lote.setLoteCantidadActual(lote.getLoteCantidadActual() - detalleDto.getCantidad());
            loteRepository.save(lote);

            // Cálculos monetarios
            BigDecimal subtotalLinea = detalleDto.getPrecioUnitario().multiply(BigDecimal.valueOf(detalleDto.getCantidad()));
            acumuladorSubtotal = acumuladorSubtotal.add(subtotalLinea);

            // Crear el objeto detalle
            VentaDetalle detalle = VentaDetalle.builder()
                    .producto(producto)
                    .lote(lote)
                    .venta(venta)
                    .detalleCantidad(detalleDto.getCantidad())
                    .detallePrecioUnitario(detalleDto.getPrecioUnitario())
                    .detalleSubtotal(subtotalLinea)
                    .build();

            venta.getDetalles().add(detalle);
        }

        // Totales Finales
        venta.setVentaSubtotal(acumuladorSubtotal);
        BigDecimal descuento = dto.getDescuento() != null ? dto.getDescuento() : BigDecimal.ZERO;
        venta.setVentaDescuento(descuento);
        venta.setVentaTotal(acumuladorSubtotal.subtract(descuento));

        // Guardar (Se guarda cabecera y detalles por el CascadeType.ALL)
        Venta guardada = ventaRepository.save(venta);
        return VentaResponseDTO.fromEntity(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponseDTO listarPorId(Long farmaciaId, Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada ID: " + id));
        return VentaResponseDTO.fromEntity(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaSimpleDTO> listarTodasPaginadas(Long farmaciaId, Pageable pageable) {
        return ventaRepository.findAll(pageable)
                .map(VentaSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaSimpleDTO> listarActivasPaginadas(Long farmaciaId, Pageable pageable) {
        return ventaRepository.findByVentaEstado(VentaEstado.completada, pageable)
                .map(VentaSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable){
        return ventaRepository.buscarPorTexto(texto, pageable)
                .map(VentaSimpleDTO::fromEntity);
    }

    @Override
    public VentaResponseDTO actualizar(Long farmaciaId, Long id, VentaUpdateDTO dto) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada ID: " + id));

        // Actualizar datos como el nombre del cliente o NIT si hubo error
        if (dto.getNombreCliente() != null) venta.setVentaNombreCliente(dto.getNombreCliente());
        if (dto.getNitCliente() != null) venta.setVentaNitCliente(dto.getNitCliente());

        Venta guardar = ventaRepository.save(venta);
        return VentaResponseDTO.fromEntity(guardar);
    }

    @Override
    public void cambiarEstado(Long farmaciaId, Long id, VentaEstado nuevoEstado) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada ID: " + id));

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
    private Sucursal buscarSucursal(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada ID: " + id));
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado ID: " + id));
    }

    private Producto buscarProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado ID: " + id));
    }

    private InventarioLotes buscarLote(Long id) {
        return loteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado ID: " + id));
    }
}