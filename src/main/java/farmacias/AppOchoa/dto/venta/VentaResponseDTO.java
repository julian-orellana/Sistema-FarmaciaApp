package farmacias.AppOchoa.dto.venta;

import farmacias.AppOchoa.dto.sucursal.SucursalSimpleDTO;
import farmacias.AppOchoa.dto.usuario.UsuarioSimpleDTO;
import farmacias.AppOchoa.dto.ventadetalle.VentaDetalleResponseDTO;
import farmacias.AppOchoa.model.Venta;
import farmacias.AppOchoa.model.VentaEstado;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VentaResponseDTO {

    private Long ventaId;
    // Datos FEL
    private String uuid;
    private String numeroAutorizacion;
    private String serie;
    private String numeroFactura;
    private LocalDateTime fechaCertificacion;

    // Cliente
    private String nitCliente;
    private String nombreCliente;

    // Relaciones
    private SucursalSimpleDTO sucursal;
    private UsuarioSimpleDTO usuario;

    // Datos venta
    private LocalDateTime fechaVenta;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal total;
    private VentaEstado estado;
    private LocalDateTime fechaCreacion;

    private List<VentaDetalleResponseDTO> detalles;

    public static VentaResponseDTO fromEntity(Venta venta) {
        if (venta == null) return null;

        return VentaResponseDTO.builder()
                .ventaId(venta.getVentaId())
                // Datos FEL - Asegúrate de que estos campos existan en Venta.java
                .uuid(venta.getVentaUuid())
                .numeroAutorizacion(venta.getVentaNumeroAutorizacion())
                .serie(venta.getVentaSerie())
                .numeroFactura(venta.getVentaNumeroFactura())
                .fechaCertificacion(venta.getVentaFechaCertificacion())
                // Cliente
                .nitCliente(venta.getVentaNitCliente())
                .nombreCliente(venta.getVentaNombreCliente())
                // Relaciones con protección contra Null
                .sucursal(venta.getSucursal() != null ?
                        SucursalSimpleDTO.fromEntity(venta.getSucursal()) : null)
                .usuario(venta.getUsuario() != null ?
                        UsuarioSimpleDTO.fromEntity(venta.getUsuario()) : null)
                // Datos venta
                .fechaVenta(venta.getVentaFecha())
                .subtotal(venta.getVentaSubtotal())
                .descuento(venta.getVentaDescuento())
                .total(venta.getVentaTotal())
                .estado(venta.getVentaEstado())
                .fechaCreacion(venta.getAuditoriaFechaCreacion())
                // Detalles
                .detalles(venta.getDetalles() != null ?
                        venta.getDetalles().stream()
                                .map(VentaDetalleResponseDTO::fromEntity)
                                .collect(Collectors.toList()) : null)
                .build();
    }
}