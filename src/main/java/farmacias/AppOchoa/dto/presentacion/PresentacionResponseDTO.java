package farmacias.AppOchoa.dto.presentacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class PresentacionResponseDTO {

    private Long presentacionId;
    private String nombre;
    private Boolean estado;

    public static PresentacionResponseDTO fromEntity (farmacias.AppOchoa.model.Presentacion presentacion){
         return PresentacionResponseDTO.builder()
                .presentacionId(presentacion.getPresentacionId())
                .nombre(presentacion.getPresentacionNombre())
                .estado(presentacion.getPresentacionEstado())
                .build();

    }
}
