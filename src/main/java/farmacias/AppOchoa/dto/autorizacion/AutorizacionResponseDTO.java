package farmacias.AppOchoa.dto.autorizacion;

import farmacias.AppOchoa.dto.usuario.UsuarioSimpleDTO;
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

public class AutorizacionResponseDTO {
    private Long autorizacionId;
    private UsuarioSimpleDTO cajero;
    private UsuarioSimpleDTO supervisor;
    private AutorizacionTipo autorizacionTipo;
    private Long autorizacionReferenciaId;
    private LocalDateTime autorizacionFecha;

    public static AutorizacionResponseDTO fromEntity(Autorizacion autorizacion){
        return AutorizacionResponseDTO.builder()
                .autorizacionId(autorizacion.getAutorizacionId())
                .cajero(autorizacion.getCajero() != null ?
                        UsuarioSimpleDTO.fromEntity(autorizacion.getCajero()): null)
                .supervisor(autorizacion.getSupervisor() != null ?
                        UsuarioSimpleDTO.fromEntity(autorizacion.getSupervisor()): null)
                .autorizacionTipo(autorizacion.getAutorizacionTipo())
                .autorizacionReferenciaId(autorizacion.getAutorizacionReferenciaId())
                .autorizacionFecha(autorizacion.getAutorizacionFecha())
                .build();
    }


}
