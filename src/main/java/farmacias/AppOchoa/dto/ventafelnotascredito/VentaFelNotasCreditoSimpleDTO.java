package farmacias.AppOchoa.dto.ventafelnotascredito;

import farmacias.AppOchoa.model.NotaEstado;
import farmacias.AppOchoa.model.VentaFelNotasCredito;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VentaFelNotasCreditoSimpleDTO {

    private Long notaId;
    private String notaUuid;
    private NotaEstado notaEstado;

    public static VentaFelNotasCreditoSimpleDTO fromEntity(VentaFelNotasCredito nota) {
        return VentaFelNotasCreditoSimpleDTO.builder()
                .notaId(nota.getNotaId())
                .notaUuid(nota.getNotaUuid())
                .notaEstado(nota.getNotaEstado())
                .build();
    }
}