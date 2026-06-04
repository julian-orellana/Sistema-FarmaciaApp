package farmacias.AppOchoa.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public void validateConfig() {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException(
                    "JWT_SECRET no está configurada. Define la variable de entorno JWT_SECRET"
            );
        }

        if (secret.length() < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET debe tener al menos 32 caracteres para ser segura"
            );
        }

        if (expiration == null || expiration <= 0) {
            throw new IllegalStateException(
                    "JWT_EXPIRATION debe ser mayor a 0"
            );
        }
    }
}