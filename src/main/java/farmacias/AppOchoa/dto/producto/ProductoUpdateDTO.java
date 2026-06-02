package farmacias.AppOchoa.dto.producto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductoUpdateDTO {

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;

    private String codigoBarras;

    @NotNull(message = "El precio de compra es obligatorio")
    private BigDecimal precioCompra;

    @NotNull(message = "El precio de venta es obligatorio")
    private BigDecimal precioVenta;

    @NotNull(message = "El porcentaje de IVA es obligatorio")
    private BigDecimal iva;

    private Boolean requiereReceta;
    private Boolean estado;
    private Long categoriaId;
    private Long presentacionId;
}