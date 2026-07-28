package farmacias.AppOchoa.integracionfel;

import farmacias.AppOchoa.model.Producto;
import farmacias.AppOchoa.model.Venta;
import farmacias.AppOchoa.model.VentaDetalle;
import farmacias.AppOchoa.model.VentaFel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DteXmlNotaCreditoBuilder {

    private static final DateTimeFormatter FECHA_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");

    // El complemento ReferenciasNota pide solo la fecha, sin hora.
    private static final DateTimeFormatter FECHA_ORIGEN_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String construir(VentaFel ventaFel){
        String fechaHoraEmision = LocalDateTime.now().format(FECHA_FORMATTER) + "-06:00";
        Venta venta = ventaFel.getVenta();

        String nitEmisor = ventaFel.getSucursal().getSucursalNit();
        String nombreEmisor = ventaFel.getSucursal().getSucursalNombre();
        String codigoEstablecimiento = ventaFel.getSucursal().getCodigoEstablecimiento();
        String afiliacionIva = ventaFel.getSucursal().getAfiliacionIva();
        String municipio = ventaFel.getSucursal().getMunicipio();
        String departamento = ventaFel.getSucursal().getDepartamento();
        String codigoPostal = ventaFel.getSucursal().getCodigoPostal();
        String sucursalDireccion = ventaFel.getSucursal().getSucursalDireccion();
        String serieOrigen = ventaFel.getFelSerie();
        String numeroOrigen = ventaFel.getFelNumeroDocumento();

        String idReceptor = venta.getVentaNitCliente();
        String nombreReceptor = venta.getVentaNombreCliente();

        // Datos del documento original al que esta nota hace referencia.
        String uuidOrigen = ventaFel.getFelUuid();
        String fechaEmisionOrigen = venta.getVentaFecha().format(FECHA_ORIGEN_FORMATTER);



        StringBuilder items = new StringBuilder();
        BigDecimal totalImpuesto = BigDecimal.ZERO;
        int numeroLinea = 1;

        for(VentaDetalle detalle : venta.getDetalles()){
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
                                <dte:DatosGenerales Tipo="NCRE" FechaHoraEmision="%s" CodigoMoneda="GTQ" />
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
                                <dte:Complementos>
                                    <dte:Complemento IDComplemento="" NombreComplemento="ReferenciasNota" URIComplemento="">
                                        <cno:ReferenciasNota Version="1" NumeroAutorizacionDocumentoOrigen="%s" FechaEmisionDocumentoOrigen="%s" SerieDocumentoOrigen="%s" NumeroDocumentoOrigen="%s" />
                                    </dte:Complemento>
                                </dte:Complementos>
                            </dte:DatosEmision>
                        </dte:DTE>
                        <dte:Adenda>
                            <DECertificador>%d</DECertificador>
                        </dte:Adenda>
                    </dte:SAT>
                </dte:GTDocumento>
                """.formatted(
                fechaHoraEmision,
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
                uuidOrigen,
                fechaEmisionOrigen,
                serieOrigen,
                numeroOrigen,
                System.currentTimeMillis()
        );
    }
}