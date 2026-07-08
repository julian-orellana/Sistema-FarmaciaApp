package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.usuario.UsuarioCreateDTO;
import farmacias.AppOchoa.dto.usuario.UsuarioResponseDTO;
import farmacias.AppOchoa.dto.usuario.UsuarioUpdateDTO;
import farmacias.AppOchoa.exception.BadRequestException;
import farmacias.AppOchoa.exception.DuplicateResourceException;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.Usuario;
import farmacias.AppOchoa.model.UsuarioRol;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.repository.SucursalRepository;
import farmacias.AppOchoa.repository.UsuarioRepository;
import farmacias.AppOchoa.serviceimpl.UsuarioServiceImpl;
import farmacias.AppOchoa.services.RefreshTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private SucursalRepository sucursalRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private FarmaciaRepository farmaciaRepository;
    @Mock
    private RefreshTokenService refreshTokenService;
    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    @DisplayName("Deberia crear un usuario correctamente cuando los datos son validos")
    void crearUsuario_ExitoCuandoDatosSonValidos(){

        Long farmaciaId = 1L;
        UsuarioCreateDTO dto = new UsuarioCreateDTO();
        dto.setNombreUsuario("steveSenior");
        dto.setContrasena("password123");
        dto.setNombre("Steve");
        dto.setApellido("Leon");
        dto.setRol(UsuarioRol.administrador);
        dto.setSucursalId(null);

        when(usuarioRepository.existsByNombreUsuarioUsuario("steveSenior"))
                .thenReturn(false);

        Farmacia farmacia = Farmacia.builder()
                .farmaciaId(farmaciaId)
                .maxUsuarios(5)
                .build();
        when(farmaciaRepository.getReferenceById(farmaciaId)).thenReturn(farmacia);
        when(usuarioRepository.countByFarmacia_FarmaciaIdAndUsuarioEstadoTrue(farmaciaId))
                .thenReturn(1L);

        when(passwordEncoder.encode("password123"))
                .thenReturn("$2a$10$hashedPassword");

        Usuario usuarioGuardado = Usuario.builder()
                .usuarioId(1L)
                .nombreUsuarioUsuario("steveSenior")
                .usuarioContrasenaHash("$2a$10$hashedPassword")
                .usuarioNombre("Steve")
                .usuarioApellido("Leon")
                .usuarioRol(UsuarioRol.administrador)
                .usuarioEstado(true)
                .sucursal(null)
                .build();

        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuarioGuardado);

        UsuarioResponseDTO resultado = usuarioService.crearUsuario(farmaciaId, dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getUsuarioId());
        assertEquals("steveSenior", resultado.getNombreUsuario());
        assertEquals("Steve", resultado.getNombre());
        assertEquals("Leon", resultado.getApellido());
        assertEquals(UsuarioRol.administrador, resultado.getRol());
        assertTrue(resultado.getEstado());

        verify(usuarioRepository, times(1))
                .existsByNombreUsuarioUsuario("steveSenior");
        verify(passwordEncoder, times(1))
                .encode("password123");
        verify(usuarioRepository, times(1))
                .save(any(Usuario.class));
        verify(sucursalRepository, never())
                .findBySucursalIdAndFarmacia_FarmaciaId(any(), any());
    }

    @Test
    @DisplayName("Deberia lanzar excepcion cuando el nombre de usuario ya existe")
    void crearUsuario_FallaCuandoNombreUsuarioYaExiste() {

        Long farmaciaId = 1L;
        UsuarioCreateDTO dto = new UsuarioCreateDTO();
        dto.setNombreUsuario("steveSenior");
        dto.setContrasena("password123");
        dto.setNombre("Steve");
        dto.setApellido("Leon");
        dto.setRol(UsuarioRol.encargado);

        when(usuarioRepository.existsByNombreUsuarioUsuario("steveSenior"))
                .thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () ->
                usuarioService.crearUsuario(farmaciaId, dto));

        assertEquals(
                "El nombre de usuario 'steveSenior' ya está en uso",
                exception.getMessage());

        verify(usuarioRepository, times(1))
                .existsByNombreUsuarioUsuario("steveSenior");
        verify(usuarioRepository, never())
                .save(any(Usuario.class));
        verify(passwordEncoder, never())
                .encode(any());
    }

    @Test
    @DisplayName("Deberia de actualizar un usuario correctamente")
    void actualizarUsuario(){

        Long farmaciaId = 1L;
        Long usuarioId = 1L;

        UsuarioUpdateDTO dto = new UsuarioUpdateDTO();
        dto.setNombreUsuario("steveSenior");
        dto.setNombre("Steve");
        dto.setApellido("Leon");
        dto.setRol(UsuarioRol.administrador);
        dto.setSucursalId(null);

        Usuario usuarioRegistrado = Usuario.builder()
                .usuarioId(1L)
                .nombreUsuarioUsuario("juanaOdont")
                .usuarioNombre("Juana")
                .usuarioApellido("Baltazar")
                .usuarioRol(UsuarioRol.administrador)
                .build();

        Usuario usuarioActualizado = Usuario.builder()
                .usuarioId(1L)
                .nombreUsuarioUsuario("juanaNico")
                .usuarioNombre("Juana Cristina")
                .usuarioApellido("Baltazar")
                .usuarioRol(UsuarioRol.administrador)
                .build();

        when(usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(1L, farmaciaId)).thenReturn(Optional.of(usuarioRegistrado));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioActualizado);

        UsuarioResponseDTO resultado  = usuarioService.actualizarUsuario(farmaciaId, usuarioId,  dto);

        assertNotNull(resultado);
        assertEquals("juanaNico", resultado.getNombreUsuario());
        assertEquals("Juana Cristina", resultado.getNombre());
        assertEquals(UsuarioRol.administrador, resultado.getRol());
        verify(usuarioRepository, times(1)).findByUsuarioIdAndFarmacia_FarmaciaId(1L, farmaciaId);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));

    }

    @Test
    @DisplayName("Deberia lanzar excepcion cuando se intenta asignar rol superadmin")
    void crearUsuario_FallaCuandoRolEsSuperadmin() {

        Long farmaciaId = 1L;
        UsuarioCreateDTO dto = new UsuarioCreateDTO();
        dto.setNombreUsuario("hackerman");
        dto.setContrasena("password123");
        dto.setNombre("Hacker");
        dto.setApellido("Man");
        dto.setRol(UsuarioRol.superadmin);

        when(usuarioRepository.existsByNombreUsuarioUsuario("hackerman"))
                .thenReturn(false);

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                usuarioService.crearUsuario(farmaciaId, dto));

        assertEquals("No se puede asignar el rol superadmin", exception.getMessage());

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deberia lanzar excepcion cuando encargado no tiene sucursal asignada")
    void crearUsuario_FallaCuandoEncargadoSinSucursal() {

        Long farmaciaId = 1L;
        UsuarioCreateDTO dto = new UsuarioCreateDTO();
        dto.setNombreUsuario("mariaEncargada");
        dto.setContrasena("password123");
        dto.setNombre("Maria");
        dto.setApellido("Lopez");
        dto.setRol(UsuarioRol.encargado);
        dto.setSucursalId(null);

        when(usuarioRepository.existsByNombreUsuarioUsuario("mariaEncargada"))
                .thenReturn(false);

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                usuarioService.crearUsuario(farmaciaId, dto));

        assertEquals("Un encargado debe tener una sucursal asignada", exception.getMessage());

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(passwordEncoder, never()).encode(any());
    }
}
