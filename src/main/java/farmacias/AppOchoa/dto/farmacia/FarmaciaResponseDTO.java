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
public class FarmaciaResponseDTO {
    private Long farmaciaId;
    private String farmaciaNombre;
    private String farmaciaNit;
    private String farmaciaEmail;
    private String farmaciaTelefono;
    private PlanTipo planTipo;
    private Boolean farmaciaActiva;
    private LocalDate pruebaHasta;
    private LocalDate suscripcionVigencia;

    public static FarmaciaResponseDTO fromEntity(Farmacia farmacia){
        return FarmaciaResponseDTO.builder()
                .farmaciaId(farmacia.getFarmaciaId())
                .farmaciaNombre(farmacia.getFarmaciaNombre())
                .farmaciaNit(farmacia.getFarmaciaNit())
                .farmaciaEmail(farmacia.getFarmaciaEmail())
                .farmaciaTelefono(farmacia.getFarmaciaTelefono())
                .planTipo(farmacia.getPlanTipo())
                .farmaciaActiva(farmacia.getFarmaciaActiva())
                .pruebaHasta(farmacia.getPruebaHasta())
                .suscripcionVigencia(farmacia.getSuscripcionVigencia())
                .build();
    }
}
