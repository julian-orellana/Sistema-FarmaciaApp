package farmacias.AppOchoa.dto.ventadetalle;

import farmacias.AppOchoa.dto.producto.ProductoSimpleDTO;
import farmacias.AppOchoa.model.VentaDetalle;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VentaDetalleResponseDTO {

    private Long detalleId;
    private ProductoSimpleDTO producto;
    private String loteNumero;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public static VentaDetalleResponseDTO fromEntity(VentaDetalle detalle) {
        // 1. Validación de seguridad para evitar NullPointerException
        if (detalle == null) return null;

        return VentaDetalleResponseDTO.builder()
                .detalleId(detalle.getDetalleId())
                // Mapeo del Producto con DTO
                .producto(detalle.getProducto() != null ?
                        ProductoSimpleDTO.fromEntity(detalle.getProducto()) : null)
                //Obtención del número de lote desde la relación InventarioLotes
                .loteNumero(detalle.getLote() != null ?
                        detalle.getLote().getLoteNumero() : "N/A")
                // Mapeo de campos numéricos (asegurando coincidencia con la Entidad)
                .cantidad(detalle.getDetalleCantidad())
                .precioUnitario(detalle.getDetallePrecioUnitario())
                .subtotal(detalle.getDetalleSubtotal())
                .build();
    }
}