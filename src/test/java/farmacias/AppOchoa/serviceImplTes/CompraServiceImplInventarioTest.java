package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.compra.CompraCreateDTO;
import farmacias.AppOchoa.dto.compradetalle.CompraDetalleCreateDTO;
import farmacias.AppOchoa.model.*;
import farmacias.AppOchoa.repository.*;
import farmacias.AppOchoa.serviceimpl.CompraServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompraServiceImpl — sincronización de inventario agregado (M9)")
public class CompraServiceImplInventarioTest {

    private static final Long FARMACIA_ID = 1L;
    private static final Long SUCURSAL_ID = 10L;
    private static final Long PRODUCTO_ID = 100L;
    private static final Long LOTE_ID = 1000L;
    private static final Long USUARIO_ID = 5L;

    @Mock private CompraRepository compraRepository;
    @Mock private SucursalRepository sucursalRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private InventarioLotesRepository loteRepository;
    @Mock private InventarioRepository inventarioRepository;
    @Mock private FarmaciaRepository farmaciaRepository;
    @InjectMocks private CompraServiceImpl compraService;

    private Farmacia farmacia;
    private Sucursal sucursal;
    private Producto producto;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        farmacia = new Farmacia();
        farmacia.setFarmaciaId(FARMACIA_ID);
        sucursal = new Sucursal();
        sucursal.setSucursalId(SUCURSAL_ID);
        sucursal.setFarmacia(farmacia);
        producto = new Producto();
        producto.setProductoId(PRODUCTO_ID);
        producto.setFarmacia(farmacia);
        usuario = new Usuario();
        usuario.setUsuarioId(USUARIO_ID);
        usuario.setFarmacia(farmacia);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, java.util.Collections.emptyList()));
    }

    private void stubCrearComun() {
        when(sucursalRepository.findBySucursalIdAndFarmacia_FarmaciaId(SUCURSAL_ID, FARMACIA_ID))
                .thenReturn(Optional.of(sucursal));
        when(usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(USUARIO_ID, FARMACIA_ID))
                .thenReturn(Optional.of(usuario));
        when(farmaciaRepository.getReferenceById(FARMACIA_ID)).thenReturn(farmacia);
        when(productoRepository.findById(PRODUCTO_ID)).thenReturn(Optional.of(producto));
        when(compraRepository.save(any(Compra.class))).thenAnswer(a -> a.getArgument(0));
    }

    private CompraCreateDTO compraDto(int cantidad) {
        CompraDetalleCreateDTO det = new CompraDetalleCreateDTO();
        det.setProductoId(PRODUCTO_ID);
        det.setCantidad(cantidad);
        det.setPrecioUnitario(new BigDecimal("4.00"));
        det.setNumeroLote("L-1");
        det.setFechaVencimiento(LocalDate.now().plusYears(1));

        CompraCreateDTO dto = new CompraCreateDTO();
        dto.setSucursalId(SUCURSAL_ID);
        dto.setFechaCompra(LocalDate.now());
        dto.setDetalles(List.of(det));
        return dto;
    }

    private InventarioLotes lote(int cantidad) {
        InventarioLotes l = new InventarioLotes();
        l.setLoteId(LOTE_ID);
        l.setLoteNumero("L-1");
        l.setLoteCantidadActual(cantidad);
        l.setProducto(producto);
        l.setSucursal(sucursal);
        l.setFarmacia(farmacia);
        return l;
    }

    @Test
    @DisplayName("Comprar incrementa el inventario agregado existente")
    void compraIncrementaInventarioExistente() {
        stubCrearComun();
        when(loteRepository.findByLoteNumeroAndSucursal_SucursalIdAndProducto_ProductoId("L-1", SUCURSAL_ID, PRODUCTO_ID))
                .thenReturn(Optional.of(lote(20)));
        Inventario inv = Inventario.builder()
                .producto(producto).sucursal(sucursal).farmacia(farmacia)
                .inventarioCantidadActual(20).inventarioCantidadMinima(5).build();
        when(inventarioRepository.findByProductoYSucursalForUpdate(PRODUCTO_ID, SUCURSAL_ID))
                .thenReturn(Optional.of(inv));

        compraService.crear(FARMACIA_ID, compraDto(8));

        assertEquals(28, inv.getInventarioCantidadActual());
        verify(inventarioRepository).save(inv);
    }

    @Test
    @DisplayName("Si no existe inventario agregado al comprar, se crea con mínima 0")
    void compraCreaInventarioSiNoExiste() {
        stubCrearComun();
        when(loteRepository.findByLoteNumeroAndSucursal_SucursalIdAndProducto_ProductoId("L-1", SUCURSAL_ID, PRODUCTO_ID))
                .thenReturn(Optional.empty());
        when(inventarioRepository.findByProductoYSucursalForUpdate(PRODUCTO_ID, SUCURSAL_ID))
                .thenReturn(Optional.empty());

        compraService.crear(FARMACIA_ID, compraDto(8));

        ArgumentCaptor<Inventario> captor = ArgumentCaptor.forClass(Inventario.class);
        verify(inventarioRepository).save(captor.capture());
        Inventario creado = captor.getValue();
        assertEquals(8, creado.getInventarioCantidadActual());
        assertEquals(0, creado.getInventarioCantidadMinima());
        assertEquals(PRODUCTO_ID, creado.getProducto().getProductoId());
        assertEquals(SUCURSAL_ID, creado.getSucursal().getSucursalId());
        assertEquals(FARMACIA_ID, creado.getFarmacia().getFarmaciaId());
    }

    @Test
    @DisplayName("Comprar setea la farmacia en la cabecera y en el lote nuevo")
    void compraSeteaFarmaciaEnCabeceraYLoteNuevo() {
        stubCrearComun();
        // Lote inexistente: crear() debe construir uno nuevo con farmacia seteada
        when(loteRepository.findByLoteNumeroAndSucursal_SucursalIdAndProducto_ProductoId("L-1", SUCURSAL_ID, PRODUCTO_ID))
                .thenReturn(Optional.empty());
        when(inventarioRepository.findByProductoYSucursalForUpdate(PRODUCTO_ID, SUCURSAL_ID))
                .thenReturn(Optional.empty());

        ArgumentCaptor<Compra> compraCaptor = ArgumentCaptor.forClass(Compra.class);
        ArgumentCaptor<InventarioLotes> loteCaptor = ArgumentCaptor.forClass(InventarioLotes.class);

        compraService.crear(FARMACIA_ID, compraDto(8));

        verify(compraRepository).save(compraCaptor.capture());
        assertNotNull(compraCaptor.getValue().getFarmacia(), "la cabecera Compra debe llevar farmacia");
        assertEquals(FARMACIA_ID, compraCaptor.getValue().getFarmacia().getFarmaciaId());

        verify(loteRepository).save(loteCaptor.capture());
        assertNotNull(loteCaptor.getValue().getFarmacia(), "el lote nuevo debe llevar farmacia");
        assertEquals(FARMACIA_ID, loteCaptor.getValue().getFarmacia().getFarmaciaId());
    }

    @Test
    @DisplayName("Anular una compra activa decrementa el inventario agregado")
    void anularCompraDecrementaInventario() {
        CompraDetalle detalle = CompraDetalle.builder()
                .detalleCantidad(8)
                .loteId(lote(28))
                .producto(producto)
                .build();
        Compra compra = Compra.builder()
                .compraEstado(CompraEstado.activa)
                .detalles(new java.util.ArrayList<>(List.of(detalle)))
                .sucursal(sucursal)
                .farmacia(farmacia)
                .build();

        when(compraRepository.findByCompraIdAndFarmacia_FarmaciaId(7L, FARMACIA_ID))
                .thenReturn(Optional.of(compra));
        when(loteRepository.findByLoteIdAndFarmaciaIdForUpdate(LOTE_ID, FARMACIA_ID))
                .thenReturn(Optional.of(lote(28)));
        Inventario inv = Inventario.builder()
                .producto(producto).sucursal(sucursal).farmacia(farmacia)
                .inventarioCantidadActual(28).inventarioCantidadMinima(0).build();
        when(inventarioRepository.findByProductoYSucursalForUpdate(PRODUCTO_ID, SUCURSAL_ID))
                .thenReturn(Optional.of(inv));

        compraService.cambiarEstado(FARMACIA_ID, 7L, CompraEstado.anulada);

        assertEquals(20, inv.getInventarioCantidadActual());
        verify(inventarioRepository).save(inv);
    }
}
