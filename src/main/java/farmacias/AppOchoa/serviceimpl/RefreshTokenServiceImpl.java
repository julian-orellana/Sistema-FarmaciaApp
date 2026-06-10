package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.exception.TokenRefreshException;
import farmacias.AppOchoa.model.RefreshToken;
import farmacias.AppOchoa.model.Usuario;
import farmacias.AppOchoa.repository.RefreshTokenRepository;
import farmacias.AppOchoa.repository.UsuarioRepository;
import farmacias.AppOchoa.services.RefreshTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;


    // 7 días en ms por default — configurable con REFRESH_TOKEN_EXPIRATION en .env
    @Value("${jwt.refresh-expiration:604800000}")
    private Long refreshExpirationMs;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository,
                                   UsuarioRepository usuarioRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.usuarioRepository = usuarioRepository;
    }

    //Crear nuevo refresh token para un usuario
    @Override
    @Transactional
    public RefreshToken crear(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new TokenRefreshException("Usuario no encontrado con ID: " + usuarioId));

        RefreshToken refreshToken = RefreshToken.builder()
                .usuario(usuario)
                .token(UUID.randomUUID().toString()) // UUID aleatorio
                .expiraEn(LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    //Verificar y rotar: valida el token, lo elimina y emite uno nuevo

    @Override
    @Transactional
    public RefreshToken verificarYRotar(String tokenStr) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new TokenRefreshException(
                        "Refresh token inválido. Inicia sesión nuevamente."));

        if (token.estaExpirado()) {
            // Limpiar el token expirado de BD antes de rechazarlo
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(
                    "El refresh token ha expirado. Inicia sesión nuevamente.");
        }

        // Si el usuario fue desactivado, revocar toda su sesión en lugar de rotar
        Usuario usuario = token.getUsuario();
        if (!usuario.isEnabled()) {
            revocarPorUsuario(usuario.getUsuarioId());
            throw new TokenRefreshException(
                    "La cuenta está desactivada. Contacta al administrador.");
        }

        // Rotación: eliminar el actual y emitir uno nuevo
        // Esto invalida cualquier intento de reuso del token anterior
        refreshTokenRepository.delete(token);

        return crear(usuario.getUsuarioId());
    }

    //Verificar token para logout (sin rotación)

    @Override
    @Transactional
    public void verificarParaLogout(String tokenStr) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new TokenRefreshException(
                        "Refresh token inválido o ya fue revocado."));

        revocarPorUsuario(token.getUsuario().getUsuarioId());
    }

    //Revocar todos los tokens del usuario (logout / cambio de contraseña / desactivación)
    @Override
    @Transactional
    public void revocarPorUsuario(Long usuarioId) {
        refreshTokenRepository.deleteByUsuarioId(usuarioId);
    }

    // Limpieza automática de tokens expirados (corre cada 24h)
    @Scheduled(cron = "0 0 3 * * *")   // 3:00 AM todos los días
    @Transactional
    public void limpiarTokensExpirados() {
        refreshTokenRepository.deleteTokensExpirados(LocalDateTime.now());
        log.info("[RefreshToken] Limpieza de tokens expirados completada");
    }
}