package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.config.JwtConfig;
import farmacias.AppOchoa.model.RefreshToken;
import farmacias.AppOchoa.model.Usuario;
import farmacias.AppOchoa.services.AuthService;
import farmacias.AppOchoa.util.JwtUtil;
import farmacias.AppOchoa.util.SuscripcionValidator;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;
    private final RefreshTokenServiceImpl refreshTokenService;

    public AuthServiceImpl(
            @Lazy AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            JwtConfig jwtConfig,
            RefreshTokenServiceImpl refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.jwtConfig = jwtConfig;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public Map<String, Object> login(String nombreUsuario, String contrasena) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(nombreUsuario, contrasena)
        );

        Usuario usuario = (Usuario) auth.getPrincipal();

        // A8: no emitir token si la farmacia está desactivada o vencida
        SuscripcionValidator.validarVigencia(usuario.getFarmacia());

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", usuario.getUsuarioId());
        claims.put("rol", usuario.getUsuarioRol().name());
        claims.put("nombre", usuario.getUsuarioNombre());
        claims.put("apellido", usuario.getUsuarioApellido());
        claims.put("farmaciaId", usuario.getFarmacia().getFarmaciaId());

        String accessToken = jwtUtil.generateToken(claims, nombreUsuario);

        // Crear refresh token en BD y devolver su UUID
        RefreshToken refreshToken = refreshTokenService.crear(usuario.getUsuarioId());

        Map<String, Object> response = new HashMap<>();
        response.put("token", accessToken);
        response.put("refreshToken", refreshToken.getToken());
        response.put("tipo", "Bearer");
        response.put("usuarioId", usuario.getUsuarioId());
        response.put("farmaciaId", usuario.getFarmacia().getFarmaciaId());
        response.put("nombreUsuario", usuario.getNombreUsuarioUsuario());
        response.put("nombreCompleto", usuario.getUsuarioNombre() + " " + usuario.getUsuarioApellido());
        response.put("rol", usuario.getUsuarioRol().name());
        response.put("expiresIn", jwtConfig.getExpiration());

        return response;
    }
}