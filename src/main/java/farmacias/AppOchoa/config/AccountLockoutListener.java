package farmacias.AppOchoa.config;

import farmacias.AppOchoa.model.Usuario;
import farmacias.AppOchoa.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


//M2: lockout por intentos fallidos.

@Component
public class AccountLockoutListener {

    private static final Logger log = LoggerFactory.getLogger(AccountLockoutListener.class);

    private static final int MAX_INTENTOS = 5;
    private static final long DURACION_BLOQUEO_MINUTOS = 15;

    private final UsuarioRepository usuarioRepository;

    public AccountLockoutListener(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Login exitoso: limpiar contador y bloqueo previos
    @EventListener
    @Transactional
    public void onSuccess(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (!(principal instanceof Usuario usuario)) {
            return;
        }
        // Releer dentro de la transacción para no persistir una entidad detached
        usuarioRepository.findByNombreUsuarioUsuario(usuario.getNombreUsuarioUsuario())
                .ifPresent(u -> {
                    if (u.getUsuarioIntentosFallidos() != 0 || u.getUsuarioBloqueadoHasta() != null) {
                        u.setUsuarioIntentosFallidos(0);
                        u.setUsuarioBloqueadoHasta(null);
                        usuarioRepository.save(u);
                    }
                });
    }

    // Credenciales inválidas: incrementar contador y bloquear si supera el umbral.
    // No se dispara cuando la cuenta ya está bloqueada (eso lanza LockedException, no Bad).
    @EventListener
    @Transactional
    public void onFailure(AuthenticationFailureBadCredentialsEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (!(event.getAuthentication() instanceof UsernamePasswordAuthenticationToken) || principal == null) {
            return;
        }
        String username = principal.toString();

        // Si el usuario no existe, no hay nada que contar (evita revelar su existencia)
        usuarioRepository.findByNombreUsuarioUsuario(username).ifPresent(usuario -> {
            // Si venía de un bloqueo ya expirado, empezar a contar de cero y limpiar la fecha
            LocalDateTime bloqueadoHasta = usuario.getUsuarioBloqueadoHasta();
            int previos = usuario.getUsuarioIntentosFallidos();
            if (bloqueadoHasta != null && bloqueadoHasta.isBefore(LocalDateTime.now())) {
                previos = 0;
                usuario.setUsuarioBloqueadoHasta(null);
            }

            int intentos = previos + 1;
            usuario.setUsuarioIntentosFallidos(intentos);

            if (intentos >= MAX_INTENTOS) {
                usuario.setUsuarioBloqueadoHasta(LocalDateTime.now().plusMinutes(DURACION_BLOQUEO_MINUTOS));
                log.warn("[Lockout] Cuenta '{}' bloqueada por {} min tras {} intentos fallidos",
                        username, DURACION_BLOQUEO_MINUTOS, intentos);
            }
            usuarioRepository.save(usuario);
        });
    }
}
