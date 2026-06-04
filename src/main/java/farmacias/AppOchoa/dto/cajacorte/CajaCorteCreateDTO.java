package farmacias.AppOchoa.dto.cajacorte;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CajaCorteCreateDTO {
    @NotNull(message = "El ID de la sesión es obligatorio")
    private Long sesionId;
    @NotNull(message = "El ID del supervisor es obligatorio")
    private Long usuarioSupervisorId;
    @DecimalMin(value = "0.0", inclusive = true, message = "El efectivo contado no puede ser negativo")
    private BigDecimal efectivoFisicoContado;
}
