package farmacias.AppOchoa.dto.autorizacion;

import farmacias.AppOchoa.model.Autorizacion;
import farmacias.AppOchoa.model.AutorizacionTipo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AutorizacionSimpleDTO {
    private Long autorizacionId;
    private AutorizacionTipo autorizacionTipo;
    private LocalDateTime autorizacionFecha;

    public static AutorizacionSimpleDTO fromEntity(Autorizacion  autorizacion){
        return AutorizacionSimpleDTO.builder()
                .autorizacionId(autorizacion.getAutorizacionId())
                .autorizacionTipo(autorizacion.getAutorizacionTipo())
                .autorizacionFecha(autorizacion.getAutorizacionFecha())
                .build();
    }
}
