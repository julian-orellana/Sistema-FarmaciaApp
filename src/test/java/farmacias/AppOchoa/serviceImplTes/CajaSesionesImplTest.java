package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.cajasesiones.CajaSesionesCreateDTO;
import farmacias.AppOchoa.dto.cajasesiones.CajaSesionesResponseDTO;
import farmacias.AppOchoa.dto.cajasesiones.CajaSesionesSimpleDTO;
import farmacias.AppOchoa.exception.BadRequestException;
import farmacias.AppOchoa.model.Caja;
import farmacias.AppOchoa.model.CajaSesiones;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.SesionEstado;
import farmacias.AppOchoa.model.Sucursal;
import farmacias.AppOchoa.model.Usuario;
import farmacias.AppOchoa.repository.CajaRepository;
import farmacias.AppOchoa.repository.CajaSesionesRepository;
import farmacias.AppOchoa.repository.SucursalRepository;
import farmacias.AppOchoa.repository.UsuarioRepository;
import farmacias.AppOchoa.serviceimpl.CajaSesionesServiceImpl;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CajaSesionesImplTest {

    @Mock
    private CajaRepository cajaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CajaSesionesRepository cajaSesionesRepository;
    @Mock
    private SucursalRepository sucursalRepository;
    @InjectMocks
    private CajaSesionesServiceImpl cajaSesionesService;

    @AfterEach
    void limpiarContextoSeguridad() {
        SecurityContextHolder.clearContext();
    }

    // Sucursal 1:1 con la farmacia; el service la resuelve desde farmaciaId
    private Sucursal sucursalDePrueba(Long farmaciaId, Long sucursalId) {
        Farmacia farmacia = new Farmacia();
        farmacia.setFarmaciaId(farmaciaId);
        Sucursal sucursal = new Sucursal();
        sucursal.setSucursalId(sucursalId);
        sucursal.setFarmacia(farmacia);
        return sucursal;
    }

    @Test
    @DisplayName("Deberia de crear un inicio de sesion en un caja exitosamente")
    void crearSesion(){

        Long farmaciaId = 1L;

        //ASSERT
        CajaSesionesCreateDTO dto = new CajaSesionesCreateDTO();
        dto.setCajaId(1L);
        dto.setSesionFondoInicial(new BigDecimal(500.00));

        Usuario usuario = new Usuario();
        usuario.setUsuarioId(1L);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null));

        Caja caja = new Caja();
        caja.setCajaId(1L);

        CajaSesiones cajaSesiones = new CajaSesiones();
        caja.setCajaId(1L);
        cajaSesiones.setSesionId(1L);
        caja.setCajaNombre("Caja1");

        Long sucursalId = 1L;
        when(sucursalRepository.findByFarmacia_FarmaciaId(farmaciaId)).thenReturn(Optional.of(sucursalDePrueba(farmaciaId, sucursalId)));
        when(usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(1L, farmaciaId)).thenReturn(Optional.of(usuario));
        when(cajaRepository.findByCajaIdAndSucursal_SucursalId(1L, sucursalId)).thenReturn(Optional.of(caja));
        when(cajaSesionesRepository.save(any(CajaSesiones.class))).thenReturn(cajaSesiones);

        CajaSesionesResponseDTO resultado = cajaSesionesService.crear(farmaciaId, dto);

        assertNotNull(resultado);
        assertEquals(cajaSesiones.getSesionId(), resultado.getSesionId());

        ArgumentCaptor<CajaSesiones> cajaSesionesArgumentCaptor = ArgumentCaptor.forClass(CajaSesiones.class);
        verify(cajaSesionesRepository).save(cajaSesionesArgumentCaptor.capture());
        CajaSesiones cajaSesiones1 = cajaSesionesArgumentCaptor.getValue();
    }

    @Test
    @DisplayName("Deberia de crear un inicio de sesion en un caja exitosamente")
    void crearSesionBuscarTexto(){

        Long farmaciaId = 1L;
        String texto = "Caja1";
        Pageable pageable = PageRequest.of(0, 10);

        // ARRANGE
        CajaSesiones cajaSesiones = new CajaSesiones();
        cajaSesiones.setSesionId(1L);

        Long sucursalId = 1L;
        Page<CajaSesiones> page = new PageImpl<>(List.of(cajaSesiones));

        when(sucursalRepository.findByFarmacia_FarmaciaId(farmaciaId)).thenReturn(Optional.of(sucursalDePrueba(farmaciaId, sucursalId)));
        when(cajaSesionesRepository.buscarPorTexto(sucursalId, texto, pageable)).thenReturn(page);

        // ACT
        Page<CajaSesionesSimpleDTO> resultado = cajaSesionesService.buscarPorTexto(farmaciaId, texto, pageable);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());

    }

    @Test
    @DisplayName("Deberia cerrar una sesion abierta seteando fecha de cierre y estado")
    void cerrarSesionExitoso(){

        Long farmaciaId = 1L;
        CajaSesiones sesion = new CajaSesiones();
        sesion.setSesionId(1L);
        sesion.setSesionEstado(SesionEstado.abierta);

        Long sucursalId = 1L;
        when(sucursalRepository.findByFarmacia_FarmaciaId(farmaciaId)).thenReturn(Optional.of(sucursalDePrueba(farmaciaId, sucursalId)));
        when(cajaSesionesRepository.findBySesionIdAndSucursal_SucursalId(1L, sucursalId)).thenReturn(Optional.of(sesion));
        when(cajaSesionesRepository.save(any(CajaSesiones.class))).thenAnswer(inv -> inv.getArgument(0));

        CajaSesionesResponseDTO resultado = cajaSesionesService.cerrar(farmaciaId, 1L);

        assertNotNull(resultado);
        ArgumentCaptor<CajaSesiones> captor = ArgumentCaptor.forClass(CajaSesiones.class);
        verify(cajaSesionesRepository).save(captor.capture());
        assertEquals(SesionEstado.cerrada, captor.getValue().getSesionEstado());
        assertNotNull(captor.getValue().getSesionFechaCierre());
    }

    @Test
    @DisplayName("Deberia rechazar el cierre de una sesion ya cerrada")
    void cerrarSesionYaCerradaFalla(){

        Long farmaciaId = 1L;
        CajaSesiones sesion = new CajaSesiones();
        sesion.setSesionId(1L);
        sesion.setSesionEstado(SesionEstado.cerrada);

        Long sucursalId = 1L;
        when(sucursalRepository.findByFarmacia_FarmaciaId(farmaciaId)).thenReturn(Optional.of(sucursalDePrueba(farmaciaId, sucursalId)));
        when(cajaSesionesRepository.findBySesionIdAndSucursal_SucursalId(1L, sucursalId)).thenReturn(Optional.of(sesion));

        assertThrows(BadRequestException.class, () -> cajaSesionesService.cerrar(farmaciaId, 1L));
        verify(cajaSesionesRepository, Mockito.never()).save(any(CajaSesiones.class));
    }

    @Test
    @DisplayName("Deberia lanzar una excepcion si se elimina una sesion")
    void eliminadoError(){
        assertThrows(UnsupportedOperationException.class, () -> {
            cajaSesionesService.eliminar(1L,1L);
        });


    }



}
