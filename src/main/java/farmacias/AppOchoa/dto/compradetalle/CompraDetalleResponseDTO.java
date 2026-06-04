package farmacias.AppOchoa.dto.compradetalle;

import farmacias.AppOchoa.dto.producto.ProductoSimpleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CompraDetalleResponseDTO {
    private Long detalleId;
    private ProductoSimpleDTO producto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subTotal;

    public static CompraDetalleResponseDTO fromEntity (farmacias.AppOchoa.model.CompraDetalle compraDetalle){
        return CompraDetalleResponseDTO.builder()
                .detalleId(compraDetalle.getDetalleId())
                .producto(compraDetalle.getProducto() != null ?
                        ProductoSimpleDTO.fromEntity(compraDetalle.getProducto()) : null)
                .cantidad(compraDetalle.getDetalleCantidad())
                .precioUnitario(compraDetalle.getDetallePrecioUnitario())
                .subTotal(compraDetalle.getDetalleSubtotal())
                .build();

    }

}
