package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.compra.CompraCreateDTO;
import farmacias.AppOchoa.dto.compra.CompraResponseDTO;
import farmacias.AppOchoa.dto.compra.CompraSimpleDTO;
import farmacias.AppOchoa.dto.compra.CompraUpdateDTO;
import farmacias.AppOchoa.dto.compradetalle.CompraDetalleCreateDTO;
import farmacias.AppOchoa.model.*;
import farmacias.AppOchoa.repository.*;
import farmacias.AppOchoa.services.CompraService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public CompraServiceImpl(
            CompraRepository compraRepository,
            SucursalRepository sucursalRepository,
            UsuarioRepository usuarioRepository,
            ProductoRepository productoRepository,
            InventarioLotesRepository loteRepository) {
        this.compraRepository = compraRepository;
        this.sucursalRepository = sucursalRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
    }

    @Override
    public CompraResponseDTO crear(Long farmaciaId, CompraCreateDTO dto) {
        Sucursal sucursal = buscarSucursal(dto.getSucursalId());
        Usuario usuario = buscarUsuario(dto.getUsuarioId());

        // 1. Crear Cabecera
        Compra compra = Compra.builder()
                .sucursal(sucursal)
                .usuario(usuario)
                .compraFecha(dto.getFechaCompra())
                .compraObservaciones(dto.getObservaciones())
                .compraEstado(CompraEstado.activa)
                .detalles(new ArrayList<>())
                .build();

        BigDecimal totalAcumulado = BigDecimal.ZERO;

        // 2. Procesar Detalles y Lotes
        for (CompraDetalleCreateDTO detDto : dto.getDetalles()) {
            Producto producto = buscarProducto(detDto.getProductoId());

            // Buscar lote existente o crear uno nuevo
            InventarioLotes lote = loteRepository.findByLoteNumero(detDto.getNumeroLote())
                    .orElseGet(() -> InventarioLotes.builder()
                            .loteNumero(detDto.getNumeroLote())
                            .loteFechaVencimiento(detDto.getFechaVencimiento())
                            .loteCantidadActual(0)
                            .loteEstado(LoteEstado.disponible)
                            .producto(producto)
                            .sucursal(sucursal)
                            .build());

            // Aumentar stock
            lote.setLoteCantidadActual(lote.getLoteCantidadActual() + detDto.getCantidad());
            loteRepository.save(lote);

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
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada ID: " + id));
        return CompraResponseDTO.fromEntity(compra);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompraSimpleDTO> listarTodasPaginadas(Long farmaciaId, Pageable pageable) {
        return compraRepository.findAll(pageable)
                .map(CompraSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompraSimpleDTO> listarActivasPaginadas(Long farmaciaId, Pageable pageable) {
        return compraRepository.findByCompraEstado(CompraEstado.activa, pageable)
                .map(CompraSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompraSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable){
        return compraRepository.buscarPorTexto(texto, pageable)
                .map(CompraSimpleDTO::fromEntity);
    }


    @Override
    public CompraResponseDTO actualizar(Long farmaciaId, Long id, CompraUpdateDTO dto) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada ID: " + id));

        if (dto.getObservaciones() != null) {
            compra.setCompraObservaciones(dto.getObservaciones());
        }

        return CompraResponseDTO.fromEntity(compraRepository.save(compra));
    }

    @Override
    public void cambiarEstado(Long farmaciaId, Long id, CompraEstado nuevoEstado) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada ID: " + id));

        // Revertir stock si se anula
        if (nuevoEstado == CompraEstado.anulada && compra.getCompraEstado() == CompraEstado.activa) {
            for (CompraDetalle detalle : compra.getDetalles()) {
                InventarioLotes lote = detalle.getLoteId();
                lote.setLoteCantidadActual(lote.getLoteCantidadActual() - detalle.getDetalleCantidad());
                loteRepository.save(lote);
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
    private Sucursal buscarSucursal(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada ID: " + id));
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado ID: " + id));
    }

    private Producto buscarProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado ID: " + id));
    }
}