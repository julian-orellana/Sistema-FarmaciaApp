package farmacias.AppOchoa.dto.presentacion;

import farmacias.AppOchoa.model.Presentacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class PresentacionSimpleDTO {

    private Long presentacionId;
    private String nombre;

    public static PresentacionSimpleDTO fromEntity (Presentacion presentacion){
        return PresentacionSimpleDTO.builder()
                .presentacionId(presentacion.getPresentacionId())
                .nombre(presentacion.getPresentacionNombre())
                .build();
    }
}
