package farmacias.AppOchoa.dto.venta;

import farmacias.AppOchoa.model.Venta;
import farmacias.AppOchoa.model.VentaEstado;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VentaSimpleDTO {

    private Long ventaId;


    private String serie;
    private String numeroFactura;

    private String nombreCliente;
    private String nombreSucursal;
    private String nombreUsuario;

    private LocalDateTime fechaVenta;
    private BigDecimal total;
    private VentaEstado estado;

    public static VentaSimpleDTO fromEntity(Venta venta) {
        return VentaSimpleDTO.builder()
                .ventaId(venta.getVentaId())
                .serie(venta.getVentaSerie())
                .numeroFactura(venta.getVentaNumeroFactura())
                .nombreCliente(venta.getVentaNombreCliente())
                .nombreSucursal(venta.getSucursal() != null ?
                        venta.getSucursal().getSucursalNombre() : null)
                .nombreUsuario(venta.getUsuario() != null ?
                        venta.getUsuario().getUsuarioNombre() + " " +
                                venta.getUsuario().getUsuarioApellido() : null)
                .fechaVenta(venta.getVentaFecha())
                .total(venta.getVentaTotal())
                .estado(venta.getVentaEstado())
                .build();
    }
}