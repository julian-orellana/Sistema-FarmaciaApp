package farmacias.AppOchoa.integracionfel;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Component
public class TekraClient {

    // URL de pruebas confirmada en el manual de TEKRA.
    // La URL de producción no está confirmada todavía.
    private static final String URL_CERTIFICACION_PRUEBAS =
            "http://apicertificacion.desa.tekra.com.gt:8080/certificacion/servicio.php";

    private final RestTemplate restTemplate = new RestTemplate();

    public String certificar(String soapEnvelope) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("SOAPAction",
                "\"http://apicertificacion.desa.tekra.com.gt:8080/certificacion/wsdl/CertificacionDocumento\"");
        headers.setContentType(new MediaType("text", "xml", StandardCharsets.UTF_8));

        HttpEntity<String> request = new HttpEntity<>(soapEnvelope, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                URL_CERTIFICACION_PRUEBAS, request, String.class
        );

        return response.getBody();
    }


    public String anular(String soapEnvelope) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("SOAPAction", "\"http://apicertificacion.desa.tekra.com.gt:8080/certificacion/wsdl/AnulacionDocumento\"");
        headers.setContentType(new MediaType("text", "xml", StandardCharsets.UTF_8));
        HttpEntity<String> request = new HttpEntity<>(soapEnvelope, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(URL_CERTIFICACION_PRUEBAS, request, String.class);
        return response.getBody();
    }
}