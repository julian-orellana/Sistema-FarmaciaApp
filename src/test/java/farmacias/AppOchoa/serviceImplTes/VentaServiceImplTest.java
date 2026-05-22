package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.venta.VentaCreateDTO;
import farmacias.AppOchoa.dto.venta.VentaResponseDTO;
import farmacias.AppOchoa.model.Sucursal;
import farmacias.AppOchoa.model.Usuario;
import farmacias.AppOchoa.model.Venta;
import farmacias.AppOchoa.model.VentaEstado;
import farmacias.AppOchoa.repository.SucursalRepository;
import farmacias.AppOchoa.repository.UsuarioRepository;
import farmacias.AppOchoa.repository.VentaRepository;
import farmacias.AppOchoa.serviceimpl.VentaServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VentaServiceImplTest {
    @Mock
    private VentaRepository ventaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private SucursalRepository sucursalRepository;
    @InjectMocks
    private VentaServiceImpl ventaService;
    @Test
    @DisplayName("Debebria crear una correcta venta, con los datos ingresados")
    void ventaExitosa(){

        Long farmaciaId = 1L;
        VentaCreateDTO dto = new VentaCreateDTO();
        dto.setSucursalId(1L);
        dto.setUsuarioId(1L);
        dto.setDetalles(new ArrayList<>());

        Sucursal sucursal = new Sucursal();
        sucursal.setSucursalId(1L);
        sucursal.setSucursalNombre("Farmacia Central Ochoa");

        Usuario usuario = new Usuario();
        usuario.setUsuarioId(1L);
        usuario.setUsuarioNombre("Pamela");
        usuario.setUsuarioApellido("Ochoa");

        Venta venta = Venta.builder()
                .ventaId(1L)
                .ventaFecha(LocalDate.of(2026, 2, 8).atStartOfDay())
                .ventaNumeroFactura("8949404")
                .ventaNitCliente("12849493")
                .ventaNombreCliente("Julian Orellana")
                .ventaSubtotal(BigDecimal.valueOf(250))
                .ventaDescuento(BigDecimal.valueOf(25))
                .ventaTotal(BigDecimal.valueOf(225))
                .sucursal(sucursal)
                .usuario(usuario)
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
        //ACT
        VentaResponseDTO resultado = ventaService.crear(farmaciaId, dto);
        //ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getVentaId());
        verify(ventaRepository).save(any(Venta.class));
   }
    @Test
    @DisplayName("Deberia de buscar una venta por ID correctamente ")
    void busquedaVentaExitosa(){

        Long farmaciaId = 1L;
        Long id = 1L;
        Venta venta = new Venta();
        venta.setVentaId(1L);
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        VentaResponseDTO resultado = ventaService.listarPorId(farmaciaId, id);
        //ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getVentaId());
        verify(ventaRepository).findById(1L);
    }

    @Test
    @DisplayName("Deberia de lanzar una excepcion al buscar una venta con ID no existente")
    void falloBusqueda(){

        Long farmaciaId = 1L;
        Long id = 1L;
        when(ventaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,() ->{
            ventaService.listarPorId(farmaciaId, 1L);
        });
        verify(ventaRepository, never()).save(any(Venta.class));
    }
    @Test
    @DisplayName("Deberia de eliminar una venta, correctamente")
    void eliminarVentaExitosa(){

        Long farmaciaId = 1L;
        Long id = 1L;
        Venta venta = new Venta();
        venta.setVentaId(1L);
        venta.setVentaEstado(VentaEstado.completada);

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        //ACT & ASSERT
        ventaService.eliminar(farmaciaId, 1L);
        ArgumentCaptor<Venta> captor = ArgumentCaptor.forClass(Venta.class);
        verify(ventaRepository).save(captor.capture());
        assertEquals(VentaEstado.anulada,captor.getValue().getVentaEstado(), "El estado deberia de cambiar a anulada");
    }
    @Test
    @DisplayName("Deberia de lanzar una excepcion al eliminar una venta, con ID incorrecto")
    void eliminarVentaFallo(){

        Long farmaciaId = 1L;
        Long id = 1L;
        when(ventaRepository.findById(1L)).thenReturn(Optional.empty());
        //ACT
        assertThrows(RuntimeException.class,() ->{
            ventaService.eliminar(farmaciaId, 1L);
        });
        verify(ventaRepository,  never()).save(any(Venta.class));
    }

}
