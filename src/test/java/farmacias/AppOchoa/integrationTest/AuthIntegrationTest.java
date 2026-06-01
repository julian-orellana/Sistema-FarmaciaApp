package farmacias.AppOchoa.integrationTest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class AuthIntegrationTest extends BaseIntegrationTest {

    // IDs que usaremos en los tests
    private Long farmaciaId;
    private Long sucursalId;

    @BeforeEach
    void prepararDatos() {
        RestAssured.baseURI = baseUrl();

        // Arrange — insertar datos mínimos para poder hacer login
        farmaciaId = testData.insertarFarmacia("Farmacia Test", "99999999");
        sucursalId = testData.insertarSucursal(farmaciaId, "Sucursal Test");
        testData.insertarAdmin(farmaciaId, sucursalId, "admin_test", "admin123");
    }

    @Test
    @DisplayName("Dado credenciales válidas, cuando hace login, entonces retorna 200 con token JWT")
    void dadoCredencialesValidas_cuandoLogin_entoncesRetorna200ConToken() {
        // Arrange — ya hecho en @BeforeEach

        // Act + Assert
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "nombreUsuario": "admin_test",
                        "contrasena": "admin123"
                    }
                    """)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("refreshToken", notNullValue())
                .body("rol", equalTo("administrador"))
                .body("farmaciaId", equalTo(farmaciaId.intValue()));
    }

    @Test
    @DisplayName("Dado credenciales incorrectas, cuando hace login, entonces retorna 401")
    void dadoCredencialesIncorrectas_cuandoLogin_entoncesRetorna401() {
        // Act + Assert
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "nombreUsuario": "admin_test",
                        "contrasena": "contrasenaMal"
                    }
                    """)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401)
                .body("mensaje", equalTo("Usuario o contraseña incorrectos"));
    }

    @Test
    @DisplayName("Dado body vacío, cuando hace login, entonces retorna 400 con errores de validación")
    void dadoBodyVacio_cuandoLogin_entoncesRetorna400() {
        // Act + Assert
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "nombreUsuario": "",
                        "contrasena": ""
                    }
                    """)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(400)
                .body("errores.nombreUsuario", notNullValue())
                .body("errores.contrasena", notNullValue());
    }
}