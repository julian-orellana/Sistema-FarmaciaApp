package farmacias.AppOchoa.dto.ventafelnotascredito;

import farmacias.AppOchoa.model.NotaEstado;
import farmacias.AppOchoa.model.VentaFelNotasCredito;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VentaFelNotasCreditoResponseDTO {

    private Long notaId;
    private Long felId;
    private String notaUuid;
    private String notaNumeroAutorizacion;
    private String notaMotivo;
    private NotaEstado notaEstado;
    private LocalDateTime auditoriaFechaCreacion;

    public static VentaFelNotasCreditoResponseDTO fromEntity(VentaFelNotasCredito nota) {
        return VentaFelNotasCreditoResponseDTO.builder()
                .notaId(nota.getNotaId())
                .felId(nota.getVentaFel() != null ? nota.getVentaFel().getFelId() : null)
                .notaUuid(nota.getNotaUuid())
                .notaNumeroAutorizacion(nota.getNotaNumeroAutorizacion())
                .notaMotivo(nota.getNotaMotivo())
                .notaEstado(nota.getNotaEstado())
                .auditoriaFechaCreacion(nota.getAuditoriaFechaCreacion())
                .build();
    }
}