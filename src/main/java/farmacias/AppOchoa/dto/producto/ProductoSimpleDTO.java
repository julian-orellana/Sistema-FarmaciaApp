package farmacias.AppOchoa.dto.producto;

import farmacias.AppOchoa.model.Producto;
import lombok.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductoSimpleDTO {

    private Long id;
    private String nombre;
    private String codigoBarras;
    private BigDecimal precioVenta;
    private String nombreCategoria;
    private Boolean requiereReceta;

    public static ProductoSimpleDTO fromEntity(Producto producto) {
        return ProductoSimpleDTO.builder()
                .id(producto.getProductoId())
                .nombre(producto.getProductoNombre())
                .codigoBarras(producto.getProductoCodigoBarras())
                .precioVenta(producto.getProductoPrecioVenta())
                .requiereReceta(producto.getProductoRequiereReceta())
                .nombreCategoria(producto.getCategoria() != null ?
                        producto.getCategoria().getCategoriaNombre() : "N/A")
                .build();
    }
}