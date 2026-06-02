package farmacias.AppOchoa.dto.alerta;

import farmacias.AppOchoa.model.Alerta;
import farmacias.AppOchoa.model.AlertaTipo;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertaSimpleDTO {

    private Long alertaId;

    private AlertaTipo tipo;

    private String mensaje;

    private LocalDateTime fecha;

    private Boolean leida;

    public static AlertaSimpleDTO fromEntity(Alerta alerta) {
        return AlertaSimpleDTO.builder()
                .alertaId(alerta.getAlertaId())
                .tipo(alerta.getAlertaTipo())
                .mensaje(alerta.getAlertaMensaje())
                .fecha(alerta.getAlertaFecha())
                .leida(alerta.getAlertaLeida())
                .build();
    }
}