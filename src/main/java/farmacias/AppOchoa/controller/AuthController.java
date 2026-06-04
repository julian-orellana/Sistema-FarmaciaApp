package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.config.JwtConfig;
import farmacias.AppOchoa.dto.token.RefreshTokenRequestDTO;
import farmacias.AppOchoa.dto.usuario.LoginDTO;
import farmacias.AppOchoa.model.RefreshToken;
import farmacias.AppOchoa.model.Usuario;
import farmacias.AppOchoa.serviceimpl.RefreshTokenServiceImpl;
import farmacias.AppOchoa.services.AuthService;
import farmacias.AppOchoa.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;

    public AuthController(AuthService authService,
                          RefreshTokenServiceImpl refreshTokenService,
                          JwtUtil jwtUtil,
                          JwtConfig jwtConfig) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
        this.jwtConfig = jwtConfig;
    }

    // Devuelve access token (24h) + refresh token (7d)

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        Map<String, Object> response = authService.login(dto.getNombreUsuario(), dto.getContrasena());
        return ResponseEntity.ok(response);
    }

    //Refresh
    //Recibe el refresh token, lo valida, lo rota y emite nuevo access token

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@Valid @RequestBody RefreshTokenRequestDTO dto) {

        // Valida el refresh token en BD y lo rota (emite uno nuevo, borra el viejo)
        RefreshToken nuevoRefreshToken = refreshTokenService.verificarYRotar(dto.getRefreshToken());

        Usuario usuario = nuevoRefreshToken.getUsuario();

        // Reconstruir los claims igual que en el login
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", usuario.getUsuarioId());
        claims.put("rol", usuario.getUsuarioRol().name());
        claims.put("nombre", usuario.getUsuarioNombre());
        claims.put("apellido", usuario.getUsuarioApellido());
        claims.put("farmaciaId", usuario.getFarmacia().getFarmaciaId());

        String nuevoAccessToken = jwtUtil.generateToken(claims, usuario.getNombreUsuarioUsuario());

        Map<String, Object> response = new HashMap<>();
        response.put("token", nuevoAccessToken);
        response.put("refreshToken", nuevoRefreshToken.getToken());
        response.put("tipo", "Bearer");
        response.put("expiresIn", jwtConfig.getExpiration());

        return ResponseEntity.ok(response);
    }

    // Revoca el refresh token — el access token expira solo (es stateless)
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@Valid @RequestBody RefreshTokenRequestDTO dto) {
        // Buscar el token en BD para obtener el usuarioId y revocar todos sus tokens
        refreshTokenService.verificarParaLogout(dto.getRefreshToken());

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Sesión cerrada correctamente");
        return ResponseEntity.ok(response);
    }
}