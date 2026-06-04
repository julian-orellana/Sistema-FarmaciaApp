package farmacias.AppOchoa.dto.ventapago;

import farmacias.AppOchoa.model.VentaPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VentaPagoSimpleDTO {
    private Long pagoId;
    private String referenciaTransaccion;
    private LocalDateTime auditoriaFechaCreacion;

    public static VentaPagoSimpleDTO fromEntity(VentaPago ventaPago) {
        return VentaPagoSimpleDTO.builder()
                .pagoId(ventaPago.getPagoId())
                .referenciaTransaccion(ventaPago.getReferenciaTransaccion())
                .auditoriaFechaCreacion(ventaPago.getAuditoriaFechaCreacion())
                .build();
    }
}