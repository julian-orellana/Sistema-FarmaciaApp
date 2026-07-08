package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.venta.VentaCobroResponseDTO;
import farmacias.AppOchoa.dto.venta.VentaCreateCobroDTO;
import farmacias.AppOchoa.dto.venta.VentaCreateDTO;
import farmacias.AppOchoa.dto.ventadetalle.VentaDetalleCreateDTO;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.*;
import farmacias.AppOchoa.repository.*;
import farmacias.AppOchoa.serviceimpl.VentaServiceImpl;
import farmacias.AppOchoa.services.KardexService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VentaServiceImpl — sincronización de inventario agregado (M9)")
public class VentaServiceImplInventarioTest {

    private static final Long FARMACIA_ID = 1L;
    private static final Long SUCURSAL_ID = 10L;
    private static final Long PRODUCTO_ID = 100L;
    private static final Long LOTE_ID = 1000L;
    private static final Long USUARIO_ID = 5L;

    private static final Long CAJA_SESION_ID = 77L;

    @Mock private VentaRepository ventaRepository;
    @Mock private SucursalRepository sucursalRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private InventarioLotesRepository loteRepository;
    @Mock private InventarioRepository inventarioRepository;
    @Mock private FarmaciaRepository farmaciaRepository;
    @Mock private VentaFelRepository ventaFelRepository;
    @Mock private VentaPagoRepository ventaPagoRepository;
    @Mock private CajaSesionesRepository cajaSesionesRepository;
    @Mock private KardexService kardexService;
    @InjectMocks private VentaServiceImpl ventaService;

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
        producto.setProductoPrecioVenta(new BigDecimal("10.00"));
        producto.setFarmacia(farmacia);

        usuario = new Usuario();
        usuario.setUsuarioId(USUARIO_ID);
        usuario.setUsuarioRol(UsuarioRol.administrador);
        usuario.setFarmacia(farmacia);

        // El cajero se toma del SecurityContext (M4)
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
    }

    private InventarioLotes lote(int cantidad) {
        InventarioLotes l = new InventarioLotes();
        l.setLoteId(LOTE_ID);
        l.setLoteNumero("L-1");
        l.setLoteCantidadActual(cantidad);
        l.setLoteEstado(LoteEstado.disponible);
        l.setProducto(producto);
        l.setSucursal(sucursal);
        l.setFarmacia(farmacia);
        return l;
    }

    private Inventario inventario(int cantidad) {
        return Inventario.builder()
                .inventarioId(1L)
                .producto(producto)
                .sucursal(sucursal)
                .farmacia(farmacia)
                .inventarioCantidadActual(cantidad)
                .inventarioCantidadMinima(0)
                .build();
    }

    private VentaCreateDTO ventaDto(int cantidad) {
        VentaDetalleCreateDTO det = new VentaDetalleCreateDTO();
        det.setProductoId(PRODUCTO_ID);
        det.setLoteId(LOTE_ID);
        det.setCantidad(cantidad);

        VentaCreateDTO dto = new VentaCreateDTO();
        dto.setSucursalId(SUCURSAL_ID);
        dto.setDetalles(List.of(det));
        return dto;
    }

    @Test
    @DisplayName("Vender decrementa el lote y también el inventario agregado")
    void ventaDecrementaInventarioAgregado() {
        when(sucursalRepository.findByFarmacia_FarmaciaId(FARMACIA_ID))
                .thenReturn(Optional.of(sucursal));
        when(usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(USUARIO_ID, FARMACIA_ID))
                .thenReturn(Optional.of(usuario));
        when(productoRepository.findById(PRODUCTO_ID)).thenReturn(Optional.of(producto));
        when(loteRepository.findByLoteIdAndSucursalIdForUpdate(LOTE_ID, SUCURSAL_ID))
                .thenReturn(Optional.of(lote(50)));
        Inventario inv = inventario(50);
        when(inventarioRepository.findByProductoYSucursalForUpdate(PRODUCTO_ID, SUCURSAL_ID))
                .thenReturn(Optional.of(inv));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(a -> a.getArgument(0));

        ventaService.crear(FARMACIA_ID, ventaDto(3));

        assertEquals(47, inv.getInventarioCantidadActual());
        verify(inventarioRepository).save(inv);
    }

    @Test
    @DisplayName("Si no existe inventario agregado al vender, se aborta con error")
    void ventaSinInventarioAgregadoFalla() {
        when(sucursalRepository.findByFarmacia_FarmaciaId(FARMACIA_ID))
                .thenReturn(Optional.of(sucursal));
        when(usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(USUARIO_ID, FARMACIA_ID))
                .thenReturn(Optional.of(usuario));
        when(productoRepository.findById(PRODUCTO_ID)).thenReturn(Optional.of(producto));
        when(loteRepository.findByLoteIdAndSucursalIdForUpdate(LOTE_ID, SUCURSAL_ID))
                .thenReturn(Optional.of(lote(50)));
        when(inventarioRepository.findByProductoYSucursalForUpdate(PRODUCTO_ID, SUCURSAL_ID))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ventaService.crear(FARMACIA_ID, ventaDto(3)));
    }

    @Test
    @DisplayName("Anular una venta completada devuelve stock al inventario agregado")
    void anularVentaIncrementaInventarioAgregado() {
        InventarioLotes lote = lote(47);
        VentaDetalle detalle = VentaDetalle.builder()
                .detalleCantidad(3)
                .lote(lote)
                .producto(producto)
                .build();
        Venta venta = Venta.builder()
                .ventaEstado(VentaEstado.completada)
                .detalles(new java.util.ArrayList<>(List.of(detalle)))
                .sucursal(sucursal)
                .build();

        when(sucursalRepository.findByFarmacia_FarmaciaId(FARMACIA_ID))
                .thenReturn(Optional.of(sucursal));
        when(ventaRepository.findByVentaIdAndSucursal_SucursalId(7L, SUCURSAL_ID))
                .thenReturn(Optional.of(venta));
        Inventario inv = inventario(47);
        when(inventarioRepository.findByProductoYSucursalForUpdate(PRODUCTO_ID, SUCURSAL_ID))
                .thenReturn(Optional.of(inv));

        ventaService.cambiarEstado(FARMACIA_ID, 7L, VentaEstado.anulada);

        assertEquals(50, inv.getInventarioCantidadActual());
        verify(inventarioRepository).save(inv);
    }

    @Test
    @DisplayName("crearConCobro (EFECTIVO) resuelve la sucursal 1:1 y persiste venta, pago y FEL")
    void crearConCobroEfectivoHappyPath() {
        // Sucursal derivada desde farmaciaId (patrón 1:1, un solo argumento)
        when(sucursalRepository.findByFarmacia_FarmaciaId(FARMACIA_ID))
                .thenReturn(Optional.of(sucursal));
        when(usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(USUARIO_ID, FARMACIA_ID))
                .thenReturn(Optional.of(usuario));
        when(farmaciaRepository.getReferenceById(FARMACIA_ID)).thenReturn(farmacia);
        when(productoRepository.findById(PRODUCTO_ID)).thenReturn(Optional.of(producto));
        when(loteRepository.findByLoteIdAndSucursalIdForUpdate(LOTE_ID, SUCURSAL_ID))
                .thenReturn(Optional.of(lote(50)));
        when(inventarioRepository.findByProductoYSucursalForUpdate(PRODUCTO_ID, SUCURSAL_ID))
                .thenReturn(Optional.of(inventario(50)));

        CajaSesiones sesion = new CajaSesiones();
        sesion.setSesionId(CAJA_SESION_ID);
        when(cajaSesionesRepository.findBySesionIdAndFarmacia_FarmaciaId(CAJA_SESION_ID, FARMACIA_ID))
                .thenReturn(Optional.of(sesion));

        when(ventaRepository.save(any(Venta.class))).thenAnswer(a -> {
            Venta v = a.getArgument(0);
            v.setVentaId(999L);
            return v;
        });
        when(ventaPagoRepository.save(any(VentaPago.class))).thenAnswer(a -> a.getArgument(0));

        VentaFel felGuardado = VentaFel.builder().felId(555L).build();
        when(ventaFelRepository.save(any(VentaFel.class))).thenReturn(felGuardado);

        // 1 unidad a 10.00 => total 10.00; el cliente paga 20.00 en efectivo
        VentaDetalleCreateDTO det = new VentaDetalleCreateDTO();
        det.setProductoId(PRODUCTO_ID);
        det.setLoteId(LOTE_ID);
        det.setCantidad(1);

        VentaCreateCobroDTO dto = new VentaCreateCobroDTO();
        dto.setSucursalId(SUCURSAL_ID);
        dto.setDetalles(List.of(det));
        dto.setCajaSesionId(CAJA_SESION_ID);
        dto.setMetodoPago(MetodoPagoEstado.EFECTIVO);
        dto.setMontoRecibido(new BigDecimal("20.00"));

        VentaCobroResponseDTO resultado = ventaService.crearConCobro(FARMACIA_ID, dto);

        assertNotNull(resultado);
        assertEquals(999L, resultado.getVentaId());
        verify(ventaRepository).save(any(Venta.class));
        verify(ventaPagoRepository).save(any(VentaPago.class));
        verify(ventaFelRepository).save(any(VentaFel.class));
    }
}
