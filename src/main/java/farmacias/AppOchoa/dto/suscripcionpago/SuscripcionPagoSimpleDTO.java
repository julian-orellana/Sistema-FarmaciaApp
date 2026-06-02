package farmacias.AppOchoa.dto.suscripcionpago;

import farmacias.AppOchoa.model.PagoEstado;
import farmacias.AppOchoa.model.PagoPlan;
import farmacias.AppOchoa.model.SuscripcionPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuscripcionPagoSimpleDTO {

    private Long pagoId;
    private BigDecimal pagoMonto;
    private PagoPlan pagoPlan;
    private LocalDate pagoPeriodoInicio;
    private LocalDate pagoPeriodoFin;
    private PagoEstado pagoEstado;

    public static SuscripcionPagoSimpleDTO fromEntity(SuscripcionPago pago) {
        return SuscripcionPagoSimpleDTO.builder()
                .pagoId(pago.getPagoId())
                .pagoMonto(pago.getPagoMonto())
                .pagoPlan(pago.getPagoPlan())
                .pagoPeriodoInicio(pago.getPagoPeriodoInicio())
                .pagoPeriodoFin(pago.getPagoPeriodoFin())
                .pagoEstado(pago.getPagoEstado())
                .build();
    }
}