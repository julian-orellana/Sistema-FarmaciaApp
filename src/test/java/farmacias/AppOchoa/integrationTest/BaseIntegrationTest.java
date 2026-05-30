package farmacias.AppOchoa.integrationTest;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
public abstract class BaseIntegrationTest {

    // Un solo contenedor MySQL compartido por todos los tests
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("farmacia_test")
            .withUsername("test")
            .withPassword("test");

    // Sobreescribe las propiedades de BD con las del contenedor
    @DynamicPropertySource
    static void configurarPropiedades(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    // Puerto aleatorio del servidor embebido
    @LocalServerPort
    protected int puerto;

    protected String baseUrl() {
        return "http://localhost:" + puerto + "/api/v1";
    }

    @Autowired
    protected TestDataBuilder testData;

    @AfterEach
    void limpiar() {
        testData.limpiarTodo();
    }
}