package farmacias.AppOchoa.dto.ventafel;

import farmacias.AppOchoa.model.VentaFel;
import farmacias.AppOchoa.model.VentaFelEstado;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VentaFelResponseDTO {

    private Long felId;
    private Long ventaId;
    private VentaFelEstado felEstado;
    private String felUuid;
    private String felNumeroAutorizacion;
    private LocalDateTime felFechaCertificacion;
    private String felErrorDescripcion;
    private Integer felIntentos;
    private LocalDateTime auditoriaFechaCreacion;

    public static VentaFelResponseDTO fromEntity(VentaFel ventaFel) {
        return VentaFelResponseDTO.builder()
                .felId(ventaFel.getFelId())
                .ventaId(ventaFel.getVenta() != null ? ventaFel.getVenta().getVentaId() : null)
                .felEstado(ventaFel.getFelEstado())
                .felUuid(ventaFel.getFelUuid())
                .felNumeroAutorizacion(ventaFel.getFelNumeroAutorizacion())
                .felFechaCertificacion(ventaFel.getFelFechaCertificacion())
                .felErrorDescripcion(ventaFel.getFelErrorDescripcion())
                .felIntentos(ventaFel.getFelIntentos())
                .auditoriaFechaCreacion(ventaFel.getAuditoriaFechaCreacion())
                .build();
    }
}