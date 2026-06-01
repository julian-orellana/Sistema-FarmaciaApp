package farmacias.AppOchoa.integrationTest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;

@Component
public class TestDataBuilder {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //Farmacia
    public Long insertarFarmacia(String nombre, String nit) {
        jdbc.update("""
                INSERT INTO farmacias (
                    farmacia_nombre, farmacia_nit, farmacia_email,
                    farmacia_telefono, plan, farmacia_activa,
                    en_periodo_prueba, max_sucursales, max_usuarios,
                    prueba_hasta, suscripcion_vigencia, auditoria_fecha_creacion
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                nombre, nit, nombre.toLowerCase() + "@test.com",
                "55551234", "pro", true,
                true, 3, 5,
                LocalDate.now().plusDays(30),
                LocalDate.now().plusDays(30),
                LocalDateTime.now()
        );
        return jdbc.queryForObject(
                "SELECT farmacia_id FROM farmacias WHERE farmacia_nit = ?",
                Long.class, nit
        );
    }

    // Sucursal

    public Long insertarSucursal(Long farmaciaId, String nombre) {
        jdbc.update("""
                INSERT INTO sucursales (
                    sucursal_nombre, sucursal_direccion, sucursal_telefono,
                    sucursal_estado, farmacia_id, auditoria_fecha_creacion
                ) VALUES (?, ?, ?, ?, ?, ?)
                """,
                nombre, "Dirección test", "55559999",
                true, farmaciaId, LocalDateTime.now()
        );
        return jdbc.queryForObject(
                "SELECT sucursal_id FROM sucursales WHERE sucursal_nombre = ? AND farmacia_id = ?",
                Long.class, nombre, farmaciaId
        );
    }

    //Usuario

    public Long insertarAdmin(Long farmaciaId, Long sucursalId,
                              String username, String contrasena) {
        String hash = passwordEncoder.encode(contrasena);
        jdbc.update("""
                INSERT INTO usuarios (
                    usuario_nombre_usuario, usuario_contrasena_hash,
                    usuario_nombre, usuario_apellido, usuario_rol,
                    usuario_estado, farmacia_id, sucursal_id,
                    auditoria_fecha_creacion
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                username, hash,
                "Admin", "Test", "administrador",
                true, farmaciaId, sucursalId,
                LocalDateTime.now()
        );
        return jdbc.queryForObject(
                "SELECT usuario_id FROM usuarios WHERE usuario_nombre_usuario = ?",
                Long.class, username
        );
    }

    //Token

    public String obtenerToken(String baseUrl, String username, String contrasena) {
        return given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "nombreUsuario": "%s",
                            "contrasena": "%s"
                        }
                        """.formatted(username, contrasena))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    // Limpieza

    public void limpiarTodo() {
        jdbc.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbc.execute("TRUNCATE TABLE refresh_tokens");
        jdbc.execute("TRUNCATE TABLE usuarios");
        jdbc.execute("TRUNCATE TABLE sucursales");
        jdbc.execute("TRUNCATE TABLE farmacias");
        jdbc.execute("SET FOREIGN_KEY_CHECKS = 1");
    }
}