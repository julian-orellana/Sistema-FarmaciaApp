package farmacias.AppOchoa.integracionfel;

import farmacias.AppOchoa.model.Venta;
import farmacias.AppOchoa.model.VentaFel;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DteXmlAnulacionBuilder {

    private static final DateTimeFormatter FECHA_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");

    private static final DateTimeFormatter FECHA_ORIGEN_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String construir(VentaFel ventaFel, String motivo){
        Venta venta = ventaFel.getVenta();

        String uuidAnular = ventaFel.getFelUuid();
        String nitEmisor = ventaFel.getSucursal().getSucursalNit();
        String idReceptor = venta.getVentaNitCliente();
        String fechaEmision = venta.getVentaFecha().format(FECHA_FORMATTER) + "-06:00";
        String fechaAnulacion = LocalDateTime.now().format(FECHA_FORMATTER) + "-06:00";

        return """
        <?xml version="1.0"?>
        <dte:GTAnulacionDocumento Version="0.1" xmlns:dte="http://www.sat.gob.gt/dte/fel/0.1.0" xmlns:ds="http://www.w3.org/2000/09/xmldsig#" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
            <dte:SAT>
                <dte:AnulacionDTE ID="DatosCertificados">
                    <dte:DatosGenerales ID="DatosAnulacion" NumeroDocumentoAAnular="%s" NITEmisor="%s" IDReceptor="%s" FechaEmisionDocumentoAnular="%s" FechaHoraAnulacion="%s" MotivoAnulacion="%s" />
                </dte:AnulacionDTE>
            </dte:SAT>
        </dte:GTAnulacionDocumento>
        """.formatted(uuidAnular, nitEmisor, idReceptor, fechaEmision, fechaAnulacion, motivo);
    }

}
