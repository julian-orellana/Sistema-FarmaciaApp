package farmacias.AppOchoa.dto.inventariolotes;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventarioLotesCreateDTO {

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La sucursal es obligatoria")
    private Long sucursalId;

    @NotBlank(message = "El número de lote es obligatorio")
    @Size(max = 50, message = "El número de lote no debe exceder 50 caracteres")
    private String numeroLote;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Future(message = "La fecha de vencimiento debe ser futura")
    private LocalDate fechaVencimiento;

    @NotNull(message = "La cantidad inicial es obligatoria")
    @Min(value = 1, message = "La cantidad inicial debe ser al menos 1")
    private Integer cantidadInicial;

    @NotNull(message = "El precio de compra es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "Formato inválido (máx 8 enteros, 2 decimales)")
    private BigDecimal precioCompra;
    
}