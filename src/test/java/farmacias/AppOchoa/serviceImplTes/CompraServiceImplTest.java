package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.compra.CompraCreateDTO;;
import farmacias.AppOchoa.dto.compra.CompraResponseDTO;
import farmacias.AppOchoa.exception.BadRequestException;
import farmacias.AppOchoa.model.Compra;
import farmacias.AppOchoa.model.CompraDetalle;
import farmacias.AppOchoa.model.CompraEstado;
import farmacias.AppOchoa.model.InventarioLotes;
import farmacias.AppOchoa.model.Sucursal;
import farmacias.AppOchoa.model.Usuario;
import farmacias.AppOchoa.repository.*;
import farmacias.AppOchoa.serviceimpl.CompraServiceImpl;
import farmacias.AppOchoa.services.KardexService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CompraServiceImplTest {
    @Mock
    private CompraRepository compraRepository;
    @Mock
    private SucursalRepository sucursalRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private InventarioLotesRepository loteRepository;
    @InjectMocks
    private CompraServiceImpl compraService;
    @Mock
    private KardexService kardexService;
    @Mock
    private FarmaciaRepository farmaciaRepository;
    @Mock
    private InventarioRepository inventarioRepository;

    @AfterEach
    void limpiarContextoSeguridad() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deberia de crear una compra correctamente con los datos registrados")
    void crearCompraExitosa(){
        Long farmaciaId = 1L;
        CompraCreateDTO dto = new CompraCreateDTO();
        dto.setFechaCompra(LocalDate.of(2024, 2, 6));
        dto.setSucursalId(1L);
        dto.setDetalles(new ArrayList<>());

        Sucursal sucursal = new Sucursal();
        sucursal.setSucursalId(1L);
        sucursal.setSucursalNombre("Farmacia Central Ochoa");

        Usuario usuario = new Usuario();
        usuario.setUsuarioId(1L);
        usuario.setNombreUsuarioUsuario("JuaSolu");
        usuario.setUsuarioNombre("Julian");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null));

        Compra compra = Compra.builder()
                .compraId(1L)
                .compraFecha(LocalDate.of(2024, 2, 6))
                .compraObservaciones("Exitosa")
                .compraTotal(BigDecimal.valueOf(12.000))
                .compraEstado(CompraEstado.activa)
                .sucursal(sucursal)
                .usuario(usuario)
                .build();

        when(sucursalRepository.findBySucursalIdAndFarmacia_FarmaciaId(1L, farmaciaId)).thenReturn(Optional.of(sucursal));
        when(usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(1L, farmaciaId)).thenReturn(Optional.of(usuario));
        when(compraRepository.save(any(Compra.class))).thenReturn(compra);
        //ACT
        CompraResponseDTO resultado = compraService.crear(farmaciaId, dto);
        //ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getCompraId());
        verify(compraRepository).save(any(Compra.class));

    }
    @Test
    @DisplayName("Deberia de elimimar (cambiar de estado) la compra")
    void eliminacionExitosa(){
        Long farmaciaId = 1L;
        Long id = 1L;
        Compra compra = new Compra();
        compra.setCompraId(1L);
        compra.setCompraEstado(CompraEstado.activa);
        when(compraRepository.findByCompraIdAndFarmacia_FarmaciaId(1L, farmaciaId)).thenReturn(Optional.of(compra));
        //ACT
        compraService.eliminar(farmaciaId, id);
        //ASSERT
        ArgumentCaptor<Compra> captor = ArgumentCaptor.forClass(Compra.class);
        verify(compraRepository).save(captor.capture());
        assertEquals(CompraEstado.anulada, captor.getValue().getCompraEstado(), "El estado deber├¡a haber cambiado a INACTIVA");
    }
    @Test
    @DisplayName("Deberia rechazar anular una compra cuyo lote ya tiene unidades vendidas")
    void anularCompraConLoteVendidoFalla(){
        Long farmaciaId = 1L;

        InventarioLotes lote = new InventarioLotes();
        lote.setLoteId(1L);
        lote.setLoteNumero("L-001");
        lote.setLoteCantidadActual(2); // de las 5 compradas ya se vendieron 3

        CompraDetalle detalle = new CompraDetalle();
        detalle.setLoteId(lote);
        detalle.setDetalleCantidad(5);

        Compra compra = new Compra();
        compra.setCompraId(1L);
        compra.setCompraEstado(CompraEstado.activa);
        compra.getDetalles().add(detalle);

        when(compraRepository.findByCompraIdAndFarmacia_FarmaciaId(1L, farmaciaId)).thenReturn(Optional.of(compra));
        when(loteRepository.findByLoteIdAndFarmaciaIdForUpdate(1L, farmaciaId)).thenReturn(Optional.of(lote));

        assertThrows(BadRequestException.class, () -> compraService.eliminar(farmaciaId, 1L));
        verify(compraRepository, Mockito.never()).save(any(Compra.class));
        verify(loteRepository, Mockito.never()).save(any(InventarioLotes.class));
    }

    @Test
    @DisplayName("Deberia de lanzar una excepcion al buscar un ID que no existe")
    void buscarFallo(){
        Long farmaciaId = 1L;
        Long id =  1L;
        when(compraRepository.findByCompraIdAndFarmacia_FarmaciaId(1L, farmaciaId)).thenReturn(Optional.empty());
        //ACT
        assertThrows(RuntimeException.class,()->{
            compraService.listarPorId(farmaciaId, 1L);
        });
    }

}
