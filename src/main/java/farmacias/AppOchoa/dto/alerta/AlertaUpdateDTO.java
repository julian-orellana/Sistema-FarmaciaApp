package farmacias.AppOchoa.dto.alerta;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertaUpdateDTO {

    @NotNull(message = "El estado de leída es obligatorio")
    private Boolean leida;
}