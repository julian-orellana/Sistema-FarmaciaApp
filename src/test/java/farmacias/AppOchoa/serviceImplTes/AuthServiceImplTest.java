package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.config.JwtConfig;
import farmacias.AppOchoa.exception.SuscripcionVencidaException;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.RefreshToken;
import farmacias.AppOchoa.model.Usuario;
import farmacias.AppOchoa.model.UsuarioRol;
import farmacias.AppOchoa.serviceimpl.AuthServiceImpl;
import farmacias.AppOchoa.serviceimpl.RefreshTokenServiceImpl;
import farmacias.AppOchoa.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private JwtConfig jwtConfig;
    @Mock
    private RefreshTokenServiceImpl refreshTokenService;
    @InjectMocks
    private AuthServiceImpl authService;

    private Usuario crearUsuarioConFarmacia(Farmacia farmacia) {
        Usuario usuario = new Usuario();
        usuario.setUsuarioId(1L);
        usuario.setNombreUsuarioUsuario("jperez");
        usuario.setUsuarioNombre("Juan");
        usuario.setUsuarioApellido("Perez");
        usuario.setUsuarioRol(UsuarioRol.administrador);
        usuario.setUsuarioEstado(true);
        usuario.setFarmacia(farmacia);
        return usuario;
    }

    private void mockAutenticacion(Usuario usuario) {
        Authentication auth = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
    }

    @Test
    @DisplayName("Deberia rechazar el login si la suscripcion de la farmacia ya vencio")
    void loginConSuscripcionVencida() {
        Farmacia farmacia = new Farmacia();
        farmacia.setFarmaciaId(1L);
        farmacia.setFarmaciaActiva(true);
        farmacia.setEnPeriodoPrueba(false);
        farmacia.setSuscripcionVigencia(LocalDate.now().minusDays(1));

        mockAutenticacion(crearUsuarioConFarmacia(farmacia));

        SuscripcionVencidaException ex = assertThrows(SuscripcionVencidaException.class,
                () -> authService.login("jperez", "secreta123"));

        assertEquals("Tu suscripción ha vencido. Contacta al administrador del sistema.", ex.getMessage());
        verify(jwtUtil, never()).generateToken(anyMap(), anyString());
        verify(refreshTokenService, never()).crear(anyLong());
    }

    @Test
    @DisplayName("Deberia rechazar el login si el periodo de prueba ya vencio")
    void loginConPeriodoPruebaVencido() {
        Farmacia farmacia = new Farmacia();
        farmacia.setFarmaciaId(1L);
        farmacia.setFarmaciaActiva(true);
        farmacia.setEnPeriodoPrueba(true);
        farmacia.setPruebaHasta(LocalDate.now().minusDays(1));

        mockAutenticacion(crearUsuarioConFarmacia(farmacia));

        SuscripcionVencidaException ex = assertThrows(SuscripcionVencidaException.class,
                () -> authService.login("jperez", "secreta123"));

        assertEquals("Tu periodo de prueba ha vencido. Contacta al administrador del sistema.", ex.getMessage());
        verify(jwtUtil, never()).generateToken(anyMap(), anyString());
    }

    @Test
    @DisplayName("Deberia rechazar el login si la farmacia esta desactivada")
    void loginConFarmaciaDesactivada() {
        Farmacia farmacia = new Farmacia();
        farmacia.setFarmaciaId(1L);
        farmacia.setFarmaciaActiva(false);

        mockAutenticacion(crearUsuarioConFarmacia(farmacia));

        SuscripcionVencidaException ex = assertThrows(SuscripcionVencidaException.class,
                () -> authService.login("jperez", "secreta123"));

        assertEquals("Tu farmacia está desactivada. Contacta al administrador del sistema.", ex.getMessage());
        verify(jwtUtil, never()).generateToken(anyMap(), anyString());
    }

    @Test
    @DisplayName("Deberia permitir el login si la suscripcion no tiene fecha limite (null)")
    void loginConVigenciaNullNoBloquea() {
        Farmacia farmacia = new Farmacia();
        farmacia.setFarmaciaId(1L);
        farmacia.setFarmaciaActiva(true);
        farmacia.setEnPeriodoPrueba(false);
        farmacia.setSuscripcionVigencia(null);

        Usuario usuario = crearUsuarioConFarmacia(farmacia);
        mockAutenticacion(usuario);

        when(jwtUtil.generateToken(anyMap(), anyString())).thenReturn("token-jwt");
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-uuid");
        when(refreshTokenService.crear(1L)).thenReturn(refreshToken);
        when(jwtConfig.getExpiration()).thenReturn(3600000L);

        Map<String, Object> resultado = authService.login("jperez", "secreta123");

        assertNotNull(resultado);
        assertEquals("token-jwt", resultado.get("token"));
        assertEquals("refresh-uuid", resultado.get("refreshToken"));
    }

    @Test
    @DisplayName("Deberia permitir el login con suscripcion vigente")
    void loginConSuscripcionVigente() {
        Farmacia farmacia = new Farmacia();
        farmacia.setFarmaciaId(1L);
        farmacia.setFarmaciaActiva(true);
        farmacia.setEnPeriodoPrueba(false);
        farmacia.setSuscripcionVigencia(LocalDate.now().plusMonths(1));

        Usuario usuario = crearUsuarioConFarmacia(farmacia);
        mockAutenticacion(usuario);

        when(jwtUtil.generateToken(anyMap(), anyString())).thenReturn("token-jwt");
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-uuid");
        when(refreshTokenService.crear(1L)).thenReturn(refreshToken);
        when(jwtConfig.getExpiration()).thenReturn(3600000L);

        Map<String, Object> resultado = authService.login("jperez", "secreta123");

        assertNotNull(resultado);
        assertEquals("token-jwt", resultado.get("token"));
    }
}
