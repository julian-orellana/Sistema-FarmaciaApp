package tmp;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;

public class DockerSmokeTest {

    @Test
    void pruebaDocker() {

        try (
            MySQLContainer<?> mysql =
                new MySQLContainer<>("mysql:8.0")
        ) {

            mysql.start();

            System.out.println(
                mysql.getJdbcUrl()
            );
        }
    }
}
