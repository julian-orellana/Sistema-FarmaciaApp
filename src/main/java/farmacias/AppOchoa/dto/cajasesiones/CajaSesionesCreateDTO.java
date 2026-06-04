package farmacias.AppOchoa.dto.cajasesiones;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CajaSesionesCreateDTO {
    @NotNull(message = "El Id de la caja es obligatorio")
    private Long cajaId;
    @NotNull(message = "El Id del usuario es obligatorio")
    private Long usuarioId;
    @NotNull(message = "El monto de apertura es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto de apertura debe ser mayor a 0")
    private BigDecimal sesionFondoInicial;

}
