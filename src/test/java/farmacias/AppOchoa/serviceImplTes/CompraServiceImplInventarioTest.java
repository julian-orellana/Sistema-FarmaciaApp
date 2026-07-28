package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.compra.CompraCreateDTO;
import farmacias.AppOchoa.dto.compradetalle.CompraDetalleCreateDTO;
import farmacias.AppOchoa.model.*;
import farmacias.AppOchoa.repository.*;
import farmacias.AppOchoa.serviceimpl.CompraServiceImpl;
import farmacias.AppOchoa.services.KardexService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Los @Mock replican EXACTAMENTE los 8 parámetros del constructor de
 * CompraServiceImpl (compra, sucursal, usuario, producto, lote, inventario,
 * farmacia, kardex). No hay CompraDetalleRepository: los detalles se persisten
 * por cascada desde Compra.
 */
@ExtendWith(MockitoExtension.class)
class CompraServiceImplInventarioTest {

    @Mock private CompraRepository compraRepository;
    @Mock private SucursalRepository sucursalRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private InventarioLotesRepository loteRepository;
    @Mock private InventarioRepository inventarioRepository;
    @Mock private FarmaciaRepository farmaciaRepository;
    @Mock private KardexService kardexService;

    @InjectMocks
    private CompraServiceImpl compraService;

    @Captor
    private ArgumentCaptor<Inventario> inventarioCaptor;

    private Farmacia farmacia;
    private Sucursal sucursal;
    private Producto producto;
    private Usuario solicitante;

    @BeforeEach
    void setUp() {
        farmacia = Farmacia.builder().farmaciaId(1L).build();

        sucursal = Sucursal.builder()
                .sucursalId(10L)
                .farmacia(farmacia)
                .build();

        // buscarProducto() filtra por p.getSucursal().getSucursalId(): sin sucursal
        // asignada el filter revienta con NPE antes de llegar al inventario.
        producto = Producto.builder()
                .productoId(100L)
                .productoNombre("Acetaminofen 500mg")
                .productoPrecioCompra(new BigDecimal("2.00"))
                .productoPrecioVenta(new BigDecimal("5.00"))
                .farmacia(farmacia)
                .sucursal(sucursal)
                .build();

        // UsuarioSimpleDTO.fromEntity() concatena nombre + apellido al mapear la
        // respuesta, por eso el usuario necesita esos campos poblados.
        solicitante = Usuario.builder()
                .usuarioId(7L)
                .nombreUsuarioUsuario("jperez")
                .usuarioNombre("Juan")
                .usuarioApellido("Perez")
                .farmacia(farmacia)
                .build();

        // crear() castea getPrincipal() a Usuario: el contexto debe llevar la entidad,
        // no un String como en un token de autenticación por username.
        Authentication auth = new UsernamePasswordAuthenticationToken(solicitante, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(sucursalRepository.findByFarmacia_FarmaciaId(1L))
                .thenReturn(Optional.of(sucursal));
        when(usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(7L, 1L))
                .thenReturn(Optional.of(solicitante));
        when(farmaciaRepository.getReferenceById(1L)).thenReturn(farmacia);
        when(productoRepository.findById(100L)).thenReturn(Optional.of(producto));

        // El lote se busca scopeado a numero+sucursal+producto; sin este stub el
        // Optional es null y orElseGet() lanza NPE.
        when(loteRepository.findByLoteNumeroAndSucursal_SucursalIdAndProducto_ProductoId(
                anyString(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(loteRepository.save(any(InventarioLotes.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        when(compraRepository.save(any(Compra.class)))
                .thenAnswer(inv -> {
                    Compra c = inv.getArgument(0);
                    if (c.getCompraId() == null) c.setCompraId(500L);
                    return c;
                });
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /**
     * crear() NO usa findByProductoAndSucursal(entidad, entidad): obtenerInventarioConLock()
     * llama findByProductoYSucursalForUpdate(productoId, sucursalId) con los IDs.
     * Stubear el método equivocado dejaba el mock devolviendo null -> NPE en orElseGet().
     */
    private void stubInventarioExistente(Optional<Inventario> resultado) {
        when(inventarioRepository.findByProductoYSucursalForUpdate(100L, 10L))
                .thenReturn(resultado);
    }

    private CompraCreateDTO buildCompraDto(int cantidad, BigDecimal precio) {
        CompraDetalleCreateDTO detalleDto = CompraDetalleCreateDTO.builder()
                .productoId(100L)
                .cantidad(cantidad)
                .precioUnitario(precio)
                .numeroLote("L-001")
                .fechaVencimiento(LocalDate.now().plusMonths(6))
                .build();

        return CompraCreateDTO.builder()
                .sucursalId(10L)
                .fechaCompra(LocalDate.now())
                .observaciones("Compra de prueba")
                .detalles(List.of(detalleDto))
                .build();
    }

    @Nested
    @DisplayName("Si no existe inventario agregado al comprar")
    class CuandoNoExisteInventario {

        @Test
        @DisplayName("Si no existe inventario agregado al comprar, se crea con mínima 0")
        void seCreaInventarioConMinimaCero() {
            stubInventarioExistente(Optional.empty());
            // Inventario nuevo (inventarioId null) -> crear() lo persiste con saveAndFlush
            // y reemplaza la entidad del mapa por la retornada.
            when(inventarioRepository.saveAndFlush(any(Inventario.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            when(inventarioRepository.save(any(Inventario.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            CompraCreateDTO dto = buildCompraDto(20, new BigDecimal("5.00"));

            compraService.crear(1L, dto);

            verify(inventarioRepository).saveAndFlush(inventarioCaptor.capture());
            Inventario creado = inventarioCaptor.getValue();

            assertThat(creado.getInventarioCantidadMinima()).isEqualTo(0);
            assertThat(creado.getProducto()).isEqualTo(producto);
            assertThat(creado.getSucursal()).isEqualTo(sucursal);
            assertThat(creado.getFarmacia()).isEqualTo(farmacia);

            // saveAndFlush ocurre ANTES de sumar la cantidad (el captor guarda la
            // referencia, así que se comprueba el estado final de la entidad).
            assertThat(creado.getInventarioCantidadActual()).isEqualTo(20);
        }

        @Test
        @DisplayName("Comprar setea la farmacia en la cabecera y en el lote nuevo")
        void seteaFarmaciaEnCabeceraYLote() {
            stubInventarioExistente(Optional.empty());
            when(inventarioRepository.saveAndFlush(any(Inventario.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            when(inventarioRepository.save(any(Inventario.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            CompraCreateDTO dto = buildCompraDto(15, new BigDecimal("3.50"));

            compraService.crear(1L, dto);

            ArgumentCaptor<Compra> compraCaptor = ArgumentCaptor.forClass(Compra.class);
            verify(compraRepository).save(compraCaptor.capture());
            assertThat(compraCaptor.getValue().getFarmacia()).isEqualTo(farmacia);

            ArgumentCaptor<InventarioLotes> loteCaptor = ArgumentCaptor.forClass(InventarioLotes.class);
            verify(loteRepository).save(loteCaptor.capture());
            InventarioLotes lote = loteCaptor.getValue();
            assertThat(lote.getFarmacia()).isEqualTo(farmacia);
            assertThat(lote.getSucursal()).isEqualTo(sucursal);
            assertThat(lote.getLoteCantidadActual()).isEqualTo(15);
        }
    }

    @Nested
    @DisplayName("Si ya existe inventario agregado al comprar")
    class CuandoYaExisteInventario {

        @Test
        @DisplayName("Comprar incrementa el inventario agregado existente")
        void incrementaInventarioExistente() {
            Inventario existente = Inventario.builder()
                    .inventarioId(900L)
                    .producto(producto)
                    .sucursal(sucursal)
                    .farmacia(farmacia)
                    .inventarioCantidadActual(10)
                    .inventarioCantidadMinima(5)
                    .build();

            stubInventarioExistente(Optional.of(existente));
            when(inventarioRepository.save(any(Inventario.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            CompraCreateDTO dto = buildCompraDto(8, new BigDecimal("2.00"));

            compraService.crear(1L, dto);

            // Ya tiene inventarioId: no se re-crea, solo se actualiza con save().
            verify(inventarioRepository, never()).saveAndFlush(any(Inventario.class));
            verify(inventarioRepository).save(existente);
            assertThat(existente.getInventarioCantidadActual()).isEqualTo(18);
            assertThat(existente.getInventarioCantidadMinima()).isEqualTo(5);
        }
    }
}
