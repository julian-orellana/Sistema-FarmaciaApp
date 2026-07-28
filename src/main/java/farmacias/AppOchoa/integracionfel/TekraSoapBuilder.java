package farmacias.AppOchoa.integracionfel;

import farmacias.AppOchoa.model.CredencialesFel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TekraSoapBuilder {
    private static final String ID_ORIGEN = "Sistema Facturacion";
    private final String ipOrigen;

    public TekraSoapBuilder(@Value("${fel.tekra.ip-origen}") String ipOrigen) {
        this.ipOrigen = ipOrigen;
    }

    public String construirSobre(CredencialesFel credenciales, String usuarioPlano, String clavePlano, String dteXml) {

        String firmarEmisor = credenciales.getFirmarEmisor() ? "SI" : "NO";
        String validarIdentificador = credenciales.getValidarIdentificador() ? "SI" : "NO";

        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <Envelope xmlns="http://schemas.xmlsoap.org/soap/envelope/">
                    <Body>
                        <CertificacionDocumento xmlns="http://apicertificacion.desa.tekra.com.gt:8080/certificacion/wsdl/">
                            <Autenticacion>
                                <pn_usuario>%s</pn_usuario>
                                <pn_clave>%s</pn_clave>
                                <pn_cliente>%d</pn_cliente>
                                <pn_contrato>%d</pn_contrato>
                                <pn_id_origen>%s</pn_id_origen>
                                <pn_ip_origen>%s</pn_ip_origen>
                                <pn_firmar_emisor>%s</pn_firmar_emisor>
                                <pn_validar_identificador>%s</pn_validar_identificador>
                            </Autenticacion>
                            <Documento>
                                <![CDATA[%s]]>
                            </Documento>
                        </CertificacionDocumento>
                    </Body>
                </Envelope>
                """.formatted(
                usuarioPlano,
                clavePlano,
                credenciales.getCredencialCliente(),
                credenciales.getCredencialContrato(),
                ID_ORIGEN,
                ipOrigen,
                firmarEmisor,
                validarIdentificador,
                dteXml
        );
    }

    public String construirSobreAnulacion(CredencialesFel credenciales, String usuarioPlano, String clavePlano, String dteXml) {
        String firmarEmisor = credenciales.getFirmarEmisor() ? "SI" : "NO";

        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <Envelope xmlns="http://schemas.xmlsoap.org/soap/envelope/">
                <Body>
                    <AnulacionDocumento xmlns="http://apicertificacion.desa.tekra.com.gt:8080/certificacion/wsdl/">
                        <Autenticacion>
                            <pn_usuario>%s</pn_usuario>
                            <pn_clave>%s</pn_clave>
                            <pn_cliente>%d</pn_cliente>
                            <pn_contrato>%d</pn_contrato>
                            <pn_id_origen>%s</pn_id_origen>
                            <pn_ip_origen>%s</pn_ip_origen>
                            <pn_firmar_emisor>%s</pn_firmar_emisor>
                        </Autenticacion>
                        <Documento>
                            <![CDATA[%s]]>
                        </Documento>
                    </AnulacionDocumento>
                </Body>
            </Envelope>
            """.formatted(
                usuarioPlano, clavePlano,
                credenciales.getCredencialCliente(), credenciales.getCredencialContrato(),
                ID_ORIGEN, ipOrigen, firmarEmisor, dteXml);
    }
}