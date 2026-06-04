package farmacias.AppOchoa.dto.producto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductoCreateDTO {

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;

    private String codigoBarras;

    @NotNull(message = "El precio de compra es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio de compra debe ser mayor a 0")
    private BigDecimal precioCompra;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio de venta debe ser mayor a 0")
    private BigDecimal precioVenta;

    @NotNull(message = "El porcentaje de IVA es obligatorio")
    @DecimalMin(value = "0.0", message = "El IVA no puede ser negativo")
    @DecimalMax(value = "100.0", message = "El IVA no puede exceder el 100%")
    private BigDecimal iva;

    private Boolean requiereReceta;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    @NotNull(message = "La presentación es obligatoria")
    private Long presentacionId;
}