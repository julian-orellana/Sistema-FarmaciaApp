package farmacias.AppOchoa.dto.ventafel;

import farmacias.AppOchoa.model.VentaFel;
import farmacias.AppOchoa.model.VentaFelEstado;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VentaFelSimpleDTO {

    private Long felId;
    private VentaFelEstado felEstado;
    private String felUuid;

    public static VentaFelSimpleDTO fromEntity(VentaFel ventaFel) {
        return VentaFelSimpleDTO.builder()
                .felId(ventaFel.getFelId())
                .felEstado(ventaFel.getFelEstado())
                .felUuid(ventaFel.getFelUuid())
                .build();
    }
}