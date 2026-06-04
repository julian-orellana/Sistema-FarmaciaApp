package farmacias.AppOchoa.dto.farmacia;

import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.PlanTipo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FarmaciaSimpleDTO {
    private Long farmaciaId;
    private String farmaciaNombre;
    private PlanTipo planTipo;
    private Boolean farmaciaActiva;
    private LocalDate suscripcionVigencia;

    public static FarmaciaSimpleDTO fromEntity(Farmacia farmacia){
        return FarmaciaSimpleDTO.builder()
                .farmaciaId(farmacia.getFarmaciaId())
                .farmaciaNombre(farmacia.getFarmaciaNombre())
                .planTipo(farmacia.getPlanTipo())
                .farmaciaActiva(farmacia.getFarmaciaActiva())
                .suscripcionVigencia(farmacia.getSuscripcionVigencia())
                .build();
    }
}
