package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.autorizacion.AutorizacionCreateDTO;
import farmacias.AppOchoa.dto.autorizacion.AutorizacionResponseDTO;
import farmacias.AppOchoa.exception.BadRequestException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.Autorizacion;
import farmacias.AppOchoa.model.AutorizacionTipo;
import farmacias.AppOchoa.model.Usuario;
import farmacias.AppOchoa.model.UsuarioRol;
import farmacias.AppOchoa.repository.AutorizacionRepository;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.repository.UsuarioRepository;
import farmacias.AppOchoa.serviceimpl.AutorizacionServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AutorizacionServiceImplTest {

    @Mock
    private AutorizacionRepository autorizacionRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private FarmaciaRepository farmaciaRepository;
    @InjectMocks
    private AutorizacionServiceImpl autorizacionService;

    private AutorizacionCreateDTO crearDto() {
        AutorizacionCreateDTO dto = new AutorizacionCreateDTO();
        dto.setAutorizacionReferenciaId(10L);
        dto.setCajeroId(1L);
        dto.setSupervisorId(2L);
        dto.setAutorizacionTipo(AutorizacionTipo.anularFactura);
        return dto;
    }

    private Usuario crearCajero() {
        Usuario cajero = new Usuario();
        cajero.setUsuarioId(1L);
        cajero.setUsuarioRol(UsuarioRol.vendedor);
        return cajero;
    }

    @Test
    @DisplayName("Deberia crear la autorizacion con un supervisor encargado de la misma farmacia")
    void crearAutorizacionExitosa() {
        Long farmaciaId = 1L;

        Usuario supervisor = new Usuario();
        supervisor.setUsuarioId(2L);
        supervisor.setUsuarioRol(UsuarioRol.encargado);

        Autorizacion autorizacion = new Autorizacion();
        autorizacion.setAutorizacionId(1L);

        when(usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(1L, farmaciaId)).thenReturn(Optional.of(crearCajero()));
        when(usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(2L, farmaciaId)).thenReturn(Optional.of(supervisor));
        when(autorizacionRepository.save(any(Autorizacion.class))).thenReturn(autorizacion);

        AutorizacionResponseDTO resultado = autorizacionService.crear(farmaciaId, crearDto());

        assertNotNull(resultado);
        verify(autorizacionRepository).save(any(Autorizacion.class));
    }

    @Test
    @DisplayName("Deberia rechazar un supervisor que no pertenece a la farmacia")
    void supervisorDeOtraFarmaciaFalla() {
        Long farmaciaId = 1L;

        when(usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(1L, farmaciaId)).thenReturn(Optional.of(crearCajero()));
        when(usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(2L, farmaciaId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> autorizacionService.crear(farmaciaId, crearDto()));
        verify(autorizacionRepository, never()).save(any(Autorizacion.class));
    }

    @Test
    @DisplayName("Deberia rechazar un supervisor con rol vendedor")
    void supervisorSinRolValidoFalla() {
        Long farmaciaId = 1L;

        Usuario supervisor = new Usuario();
        supervisor.setUsuarioId(2L);
        supervisor.setUsuarioRol(UsuarioRol.vendedor);

        when(usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(1L, farmaciaId)).thenReturn(Optional.of(crearCajero()));
        when(usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(2L, farmaciaId)).thenReturn(Optional.of(supervisor));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> autorizacionService.crear(farmaciaId, crearDto()));

        assertEquals("El supervisor debe tener rol administrador o encargado", ex.getMessage());
        verify(autorizacionRepository, never()).save(any(Autorizacion.class));
    }
}
