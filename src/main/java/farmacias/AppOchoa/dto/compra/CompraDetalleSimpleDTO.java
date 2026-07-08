package farmacias.AppOchoa.dto.compra;


import farmacias.AppOchoa.dto.producto.ProductoSimpleDTO;
import farmacias.AppOchoa.model.CompraDetalle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompraDetalleSimpleDTO {

    private Long detalleId;
    private ProductoSimpleDTO producto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subTotal;

    public static CompraDetalleSimpleDTO fromEntity(CompraDetalle compraDetalle){
        return CompraDetalleSimpleDTO.builder()
                .detalleId(compraDetalle.getDetalleId())
                .producto(compraDetalle.getProducto() != null ?
                        ProductoSimpleDTO.fromEntity(compraDetalle.getProducto()) : null)
                .cantidad(compraDetalle.getDetalleCantidad())
                .precioUnitario(compraDetalle.getDetallePrecioUnitario())
                .subTotal(compraDetalle.getDetalleSubtotal())
                .build();
    }

}
