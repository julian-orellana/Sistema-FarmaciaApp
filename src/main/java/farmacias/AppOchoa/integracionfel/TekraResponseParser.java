package farmacias.AppOchoa.integracionfel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

@Component
public class TekraResponseParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public TekraCertificacionResultado parsear(String respuestaSoap) {
        try {
            Document doc = parseXml(respuestaSoap);

            String resultadoJson = textoDeEtiqueta(doc, "ResultadoCertificacion");
            JsonNode resultado = objectMapper.readTree(resultadoJson);
            int codigoError = resultado.get("error").asInt();

            if (codigoError != 0) {
                String detalle = resultado.has("errores_xsd") && !resultado.get("errores_xsd").isNull()
                        ? resultado.get("errores_xsd").toString()
                        : resultadoJson;
                return TekraCertificacionResultado.error(codigoError, detalle);
            }

            // El UUID/Serie/Numero salen de dte:NumeroAutorizacion, anidado dentro de
            // DocumentoCertificado. El manual también menciona campos "NumeroAutorizacion"/
            // "SerieDocumento" sueltos a nivel superior, pero no aparecen así en el
            // ejemplo real que tenemos — falta confirmar contra una respuesta real de TEKRA.
            Element numeroAutorizacionEl = (Element) doc.getElementsByTagName("dte:NumeroAutorizacion").item(0);
            String uuid = numeroAutorizacionEl.getTextContent();
            String serie = numeroAutorizacionEl.getAttribute("Serie");
            String numero = numeroAutorizacionEl.getAttribute("Numero");

            String fechaCertificacion = textoDeEtiqueta(doc, "dte:FechaHoraCertificacion");

            String xmlCertificado = extraerEntre(respuestaSoap, "<DocumentoCertificado>", "</DocumentoCertificado>");

            return TekraCertificacionResultado.exito(uuid, serie, numero, fechaCertificacion, xmlCertificado);

        } catch (Exception e) {
            throw new RuntimeException("Error al parsear la respuesta de TEKRA", e);
        }
    }

    private Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xml)));
    }

    private String textoDeEtiqueta(Document doc, String tagName) {
        NodeList nodes = doc.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            throw new RuntimeException("No se encontró la etiqueta " + tagName + " en la respuesta de TEKRA");
        }
        return nodes.item(0).getTextContent();
    }

    private String extraerEntre(String texto, String inicio, String fin) {
        int desde = texto.indexOf(inicio);
        int hasta = texto.indexOf(fin);
        if (desde == -1 || hasta == -1) {
            return null;
        }
        return texto.substring(desde + inicio.length(), hasta);
    }
}