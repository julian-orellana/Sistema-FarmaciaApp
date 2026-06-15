package farmacias.AppOchoa.config;

import farmacias.AppOchoa.model.Usuario;
import farmacias.AppOchoa.model.UsuarioRol;
import farmacias.AppOchoa.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Order(2)
public class SuperAdminInitializer {

    private static final Logger log = LoggerFactory.getLogger(SuperAdminInitializer.class);

    @Value("${SUPERADMIN_USER:}")
    private String superadminUser;

    @Value("${SUPERADMIN_PASS:}")
    private String superadminPass;

    @Bean
    public CommandLineRunner initSuperAdmin(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            if (superadminUser == null || superadminUser.isBlank() ||
                    superadminPass == null || superadminPass.isBlank()) {
                log.info("[SuperAdminInitializer] Variables SUPERADMIN_USER/PASS no configuradas. Omitiendo.");
                return;
            }

            if (usuarioRepository.existsByUsuarioRol(UsuarioRol.superadmin)) {
                log.info("[SuperAdminInitializer] Superadmin ya existe. Omitiendo bootstrap.");
                return;
            }

            Usuario superadmin = Usuario.builder()
                    .nombreUsuarioUsuario(superadminUser)
                    .usuarioContrasenaHash(passwordEncoder.encode(superadminPass))
                    .usuarioNombre("Super")
                    .usuarioApellido("Admin")
                    .usuarioRol(UsuarioRol.superadmin)
                    .usuarioEstado(true)
                    .farmacia(null)
                    .sucursal(null)
                    .build();

            usuarioRepository.save(superadmin);
            log.warn("[SuperAdminInitializer] Superadmin '{}' creado exitosamente.", superadminUser);
        };
    }
}