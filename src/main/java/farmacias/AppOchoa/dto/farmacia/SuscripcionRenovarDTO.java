package farmacias.AppOchoa.dto.farmacia;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuscripcionRenovarDTO {

    @NotNull(message = "La nueva vigencia de la suscripción es obligatoria")
    private LocalDate nuevaVigencia;
}
