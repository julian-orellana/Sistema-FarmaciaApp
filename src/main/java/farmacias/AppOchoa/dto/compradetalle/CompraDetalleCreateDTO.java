package farmacias.AppOchoa.dto.compradetalle;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompraDetalleCreateDTO {

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "Formato inválido")
    private BigDecimal precioUnitario;

    // Datos del lote (para crear el lote en inventario)
    @NotBlank(message = "El número de lote es obligatorio")
    @Size(max = 50, message = "El número de lote no debe exceder 50 caracteres")
    private String numeroLote;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate fechaVencimiento;

}