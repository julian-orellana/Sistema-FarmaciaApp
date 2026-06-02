package farmacias.AppOchoa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AppOchoaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppOchoaApplication.class, args);
	}

}