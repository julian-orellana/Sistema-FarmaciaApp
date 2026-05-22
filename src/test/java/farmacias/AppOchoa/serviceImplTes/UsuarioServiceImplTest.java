package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.usuario.UsuarioCreateDTO;
import farmacias.AppOchoa.dto.usuario.UsuarioResponseDTO;
import farmacias.AppOchoa.dto.usuario.UsuarioUpdateDTO;
import farmacias.AppOchoa.model.Usuario;
import farmacias.AppOchoa.model.UsuarioRol;
import farmacias.AppOchoa.repository.SucursalRepository;
import farmacias.AppOchoa.repository.UsuarioRepository;
import farmacias.AppOchoa.serviceimpl.UsuarioServiceImpl;
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
        dto.setRol(UsuarioRol.encargado);
        dto.setSucursalId(null);

        when(usuarioRepository.existsByNombreUsuarioUsuario("steveSenior"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("$2a$10$hashedPassword");

        Usuario usuarioGuardado = Usuario.builder()
                .usuarioId(1L)
                .nombreUsuarioUsuario("steveSenior")
                .usuarioContrasenaHash("$2a$10$hashedPassword")
                .usuarioNombre("Steve")
                .usuarioApellido("Leon")
                .usuarioRol(UsuarioRol.encargado)
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
        assertEquals(UsuarioRol.encargado, resultado.getRol());
        assertTrue(resultado.getEstado());

        verify(usuarioRepository, times(1))
                .existsByNombreUsuarioUsuario("steveSenior");
        verify(passwordEncoder, times(1))
                .encode("password123");
        verify(usuarioRepository, times(1))
                .save(any(Usuario.class));
        verify(sucursalRepository, never())
                .findById(any());
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

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioService.crearUsuario(farmaciaId, dto)
        );

        assertEquals(
                "El nombre de usuario 'steveSenior' ya esta en uso",
                exception.getMessage()
        );

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
        dto.setRol(UsuarioRol.encargado);
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

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioRegistrado));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioActualizado);

        UsuarioResponseDTO resultado  = usuarioService.actualizarUsuario(farmaciaId, usuarioId,  dto);

        assertNotNull(resultado);
        assertEquals("juanaNico", resultado.getNombreUsuario());
        assertEquals("Juana Cristina", resultado.getNombre());
        assertEquals(UsuarioRol.administrador, resultado.getRol());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));



    }
}
