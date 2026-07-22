package farmacias.AppOchoa.integracionfel;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TekraClient {

    // URL de pruebas confirmada en el manual de TEKRA.
    // La URL de producción no está confirmada todavía.
    private static final String URL_CERTIFICACION_PRUEBAS =
            "http://apicertificacion.desa.tekra.com.gt:8080/certificacion/servicio.php";

    private final RestTemplate restTemplate = new RestTemplate();

    public String certificar(String soapEnvelope) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);

        HttpEntity<String> request = new HttpEntity<>(soapEnvelope, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                URL_CERTIFICACION_PRUEBAS, request, String.class
        );

        return response.getBody();
    }
}