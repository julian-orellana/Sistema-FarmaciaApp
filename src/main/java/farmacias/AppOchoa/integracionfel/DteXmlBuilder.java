package farmacias.AppOchoa.integracionfel;

import farmacias.AppOchoa.model.Producto;
import farmacias.AppOchoa.model.Venta;
import farmacias.AppOchoa.model.VentaDetalle;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.format.DateTimeFormatter;

@Component
public class DteXmlBuilder {

    private static final DateTimeFormatter FECHA_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");

    public String construir(Venta venta) {
        String fechaHoraEmision = venta.getVentaFecha().format(FECHA_FORMATTER) + "-06:00";
        int numeroAcceso = new SecureRandom().nextInt(900_000_000) + 100_000_000;

        String nitEmisor = venta.getSucursal().getSucursalNit();
        String nombreEmisor = venta.getSucursal().getSucursalNombre();
        String codigoEstablecimiento = venta.getSucursal().getCodigoEstablecimiento();
        String afiliacionIva = venta.getSucursal().getAfiliacionIva();
        String municipio = venta.getSucursal().getMunicipio();
        String departamento = venta.getSucursal().getDepartamento();
        String codigoPostal = venta.getSucursal().getCodigoPostal();
        String sucursalDireccion = venta.getSucursal().getSucursalDireccion();

        String idReceptor = venta.getVentaNitCliente();
        String nombreReceptor = venta.getVentaNombreCliente();

        StringBuilder items = new StringBuilder();
        BigDecimal totalImpuesto = BigDecimal.ZERO;
        int numeroLinea = 1;

        for (VentaDetalle detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            BigDecimal precio = detalle.getDetalleSubtotal();
            BigDecimal tasaIva = producto.getProductoIva();

            BigDecimal divisor = BigDecimal.ONE.add(tasaIva.divide(BigDecimal.valueOf(100)));
            BigDecimal montoGravable = precio.divide(divisor, 5, RoundingMode.HALF_UP);
            BigDecimal montoImpuesto = precio.subtract(montoGravable);
            totalImpuesto = totalImpuesto.add(montoImpuesto);

            items.append("""
                    <dte:Item NumeroLinea="%d" BienOServicio="B">
                        <dte:Cantidad>%d</dte:Cantidad>
                        <dte:Descripcion>%s</dte:Descripcion>
                        <dte:PrecioUnitario>%s</dte:PrecioUnitario>
                        <dte:Precio>%s</dte:Precio>
                        <dte:Descuento>0.00</dte:Descuento>
                        <dte:Impuestos>
                            <dte:Impuesto>
                                <dte:NombreCorto>IVA</dte:NombreCorto>
                                <dte:CodigoUnidadGravable>1</dte:CodigoUnidadGravable>
                                <dte:MontoGravable>%s</dte:MontoGravable>
                                <dte:MontoImpuesto>%s</dte:MontoImpuesto>
                            </dte:Impuesto>
                        </dte:Impuestos>
                        <dte:Total>%s</dte:Total>
                    </dte:Item>
                    """.formatted(numeroLinea, detalle.getDetalleCantidad(), producto.getProductoNombre(),
                    detalle.getDetallePrecioUnitario(), precio, montoGravable, montoImpuesto, precio));
            numeroLinea++;
        }

        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <dte:GTDocumento Version="0.1" xmlns:dte="http://www.sat.gob.gt/dte/fel/0.2.0"
                    xmlns:cfc="http://www.sat.gob.gt/dte/fel/CompCambiaria/0.1.0"
                    xmlns:cex="http://www.sat.gob.gt/face2/ComplementoExportaciones/0.1.0"
                    xmlns:cfe="http://www.sat.gob.gt/face2/ComplementoFacturaEspecial/0.1.0"
                    xmlns:cno="http://www.sat.gob.gt/face2/ComplementoReferenciaNota/0.1.0"
                    xmlns:ds="http://www.w3.org/2000/09/xmldsig#"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                    <dte:SAT ClaseDocumento="dte">
                        <dte:DTE ID="DatosCertificados">
                            <dte:DatosEmision ID="DatosEmision">
                                <dte:DatosGenerales Tipo="FACT" FechaHoraEmision="%s" CodigoMoneda="GTQ" NumeroAcceso="%d" />
                                <dte:Emisor NITEmisor="%s" NombreEmisor="%s" CodigoEstablecimiento="%s" NombreComercial="%s" CorreoEmisor="" AfiliacionIVA="%s">
                                    <dte:DireccionEmisor>
                                        <dte:Direccion>%s</dte:Direccion>
                                        <dte:CodigoPostal>%s</dte:CodigoPostal>
                                        <dte:Municipio>%s</dte:Municipio>
                                        <dte:Departamento>%s</dte:Departamento>
                                        <dte:Pais>GT</dte:Pais>
                                    </dte:DireccionEmisor>
                                </dte:Emisor>
                                <dte:Receptor IDReceptor="%s" NombreReceptor="%s" CorreoReceptor="">
                                    <dte:DireccionReceptor>
                                        <dte:Direccion>Ciudad</dte:Direccion>
                                        <dte:CodigoPostal>0</dte:CodigoPostal>
                                        <dte:Municipio></dte:Municipio>
                                        <dte:Departamento></dte:Departamento>
                                        <dte:Pais>GT</dte:Pais>
                                    </dte:DireccionReceptor>
                                </dte:Receptor>
                                <!-- TipoFrase=1 / CodigoEscenario=1: confirmado por TEKRA para su propio NIT/régimen GEN,
                                     válido mientras factures bajo el NIT autorizado de TEKRA. -->
                                <dte:Frases>
                                    <dte:Frase TipoFrase="1" CodigoEscenario="1" />
                                </dte:Frases>
                                <dte:Items>
                                %s
                                </dte:Items>
                                <dte:Totales>
                                    <dte:TotalImpuestos>
                                        <dte:TotalImpuesto NombreCorto="IVA" TotalMontoImpuesto="%s" />
                                    </dte:TotalImpuestos>
                                    <dte:GranTotal>%s</dte:GranTotal>
                                </dte:Totales>
                            </dte:DatosEmision>
                        </dte:DTE>
                        <dte:Adenda>
                            <DECertificador>%d</DECertificador>
                        </dte:Adenda>
                    </dte:SAT>
                </dte:GTDocumento>
                """.formatted(
                fechaHoraEmision,
                numeroAcceso,
                nitEmisor,
                nombreEmisor,
                codigoEstablecimiento,
                nombreEmisor,
                afiliacionIva,
                sucursalDireccion,
                codigoPostal,
                municipio,
                departamento,
                idReceptor,
                nombreReceptor,
                items.toString(),
                totalImpuesto,
                venta.getVentaTotal(),
                venta.getVentaId()
        );
    }
}