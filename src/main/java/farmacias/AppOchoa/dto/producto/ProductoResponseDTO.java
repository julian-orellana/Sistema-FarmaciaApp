package farmacias.AppOchoa.dto.producto;

import farmacias.AppOchoa.model.Producto;
import lombok.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductoResponseDTO {

    private Long id;
    private String nombre;
    private String codigoBarras;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private BigDecimal iva; // Campo corregido
    private Boolean requiereReceta;
    private Boolean estado;
    private String nombreCategoria;
    private String nombrePresentacion;

    public static ProductoResponseDTO fromEntity(Producto producto) {
        return ProductoResponseDTO.builder()
                .id(producto.getProductoId())
                .nombre(producto.getProductoNombre())
                .codigoBarras(producto.getProductoCodigoBarras())
                .precioCompra(producto.getProductoPrecioCompra())
                .precioVenta(producto.getProductoPrecioVenta())
                .iva(producto.getProductoIva())
                .requiereReceta(producto.getProductoRequiereReceta())
                .estado(producto.getProductoEstado())
                .nombreCategoria(producto.getCategoria() != null ?
                        producto.getCategoria().getCategoriaNombre() : "Sin Categoría")
                .nombrePresentacion(producto.getPresentacion() != null ?
                        producto.getPresentacion().getPresentacionNombre() : "Sin Presentación")
                .build();
    }
}