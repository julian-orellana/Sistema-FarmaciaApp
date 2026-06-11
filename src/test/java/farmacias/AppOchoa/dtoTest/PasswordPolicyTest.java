package farmacias.AppOchoa.dtoTest;

import farmacias.AppOchoa.dto.usuario.CambiarContrasenaDTO;
import farmacias.AppOchoa.dto.usuario.UsuarioCreateDTO;
import farmacias.AppOchoa.model.UsuarioRol;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * B3: la política de contraseñas (min 10, mayúscula + minúscula + número) se
 * expresa con anotaciones Bean Validation en los DTOs. Aquí se valida con un
 * Validator programático que esas reglas rechazan contraseñas débiles.
 */
@DisplayName("Política de contraseñas (B3)")
class PasswordPolicyTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void init() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void close() {
        factory.close();
    }

    private UsuarioCreateDTO usuarioConContrasena(String pwd) {
        return UsuarioCreateDTO.builder()
                .nombreUsuario("usuario1")
                .contrasena(pwd)
                .nombre("Nombre")
                .apellido("Apellido")
                .rol(UsuarioRol.encargado)
                .sucursalId(1L)
                .build();
    }

    private boolean contrasenaTieneViolacion(String pwd) {
        Set<ConstraintViolation<UsuarioCreateDTO>> v =
                validator.validateProperty(usuarioConContrasena(pwd), "contrasena");
        return !v.isEmpty();
    }

    @ParameterizedTest
    @DisplayName("Contraseñas débiles son rechazadas")
    @ValueSource(strings = {
            "Abc12",          // muy corta
            "abcdefghij1",    // sin mayúscula
            "ABCDEFGHIJ1",    // sin minúscula
            "Abcdefghij",     // sin número
            "password123"     // sin mayúscula (10+ pero incumple complejidad)
    })
    void contrasenaDebilRechazada(String pwd) {
        assertTrue(contrasenaTieneViolacion(pwd), "debería rechazarse: " + pwd);
    }

    @ParameterizedTest
    @DisplayName("Contraseñas válidas son aceptadas")
    @ValueSource(strings = {
            "Abcdefghi1",     // exactamente 10, cumple complejidad
            "MiClaveSegura2026"
    })
    void contrasenaValidaAceptada(String pwd) {
        assertFalse(contrasenaTieneViolacion(pwd), "debería aceptarse: " + pwd);
    }

    @Test
    @DisplayName("CambiarContrasenaDTO aplica la misma regla")
    void cambioContrasenaMismaRegla() {
        CambiarContrasenaDTO debil = CambiarContrasenaDTO.builder()
                .contrasenaActual("loQueSea1A")
                .contrasenaNueva("password123") // sin mayúscula
                .build();
        assertFalse(validator.validateProperty(debil, "contrasenaNueva").isEmpty());

        CambiarContrasenaDTO fuerte = CambiarContrasenaDTO.builder()
                .contrasenaActual("loQueSea1A")
                .contrasenaNueva("Abcdefghi1")
                .build();
        assertTrue(validator.validateProperty(fuerte, "contrasenaNueva").isEmpty());
    }
}
