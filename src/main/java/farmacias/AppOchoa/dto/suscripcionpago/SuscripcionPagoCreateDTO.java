package farmacias.AppOchoa.dto.suscripcionpago;

import farmacias.AppOchoa.model.PagoMetodo;
import farmacias.AppOchoa.model.PagoPlan;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuscripcionPagoCreateDTO {

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor que 0")
    private BigDecimal pagoMonto;

    @NotNull(message = "La asignacion del metodo es obligatoria")
    private PagoMetodo pagoMetodo;

    @NotNull(message = "La asignacion del plan es obligatoria")
    private PagoPlan pagoPlan;
    private String pagoReferencia;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate pagoPeriodoInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate pagoPeriodoFin;
    private String pagoNotas;
}