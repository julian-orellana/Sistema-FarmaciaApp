package farmacias.AppOchoa.dto.suscripcionpago;

import farmacias.AppOchoa.dto.farmacia.FarmaciaSimpleDTO;
import farmacias.AppOchoa.model.PagoEstado;
import farmacias.AppOchoa.model.PagoMetodo;
import farmacias.AppOchoa.model.PagoPlan;
import farmacias.AppOchoa.model.SuscripcionPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuscripcionPagoResponseDTO {

    private Long pagoId;
    private FarmaciaSimpleDTO farmacia;
    private BigDecimal pagoMonto;
    private PagoPlan pagoPlan;
    private PagoMetodo pagoMetodo;
    private String pagoReferencia;
    private LocalDate pagoPeriodoInicio;
    private LocalDate pagoPeriodoFin;
    private PagoEstado pagoEstado;
    private String pagoNotas;
    private LocalDateTime auditoriaFechaCreacion;

    public static SuscripcionPagoResponseDTO fromEntity(SuscripcionPago pago) {
        return SuscripcionPagoResponseDTO.builder()
                .pagoId(pago.getPagoId())
                .farmacia(pago.getFarmacia() != null ?
                        FarmaciaSimpleDTO.fromEntity(pago.getFarmacia()) : null)
                .pagoMonto(pago.getPagoMonto())
                .pagoPlan(pago.getPagoPlan())
                .pagoMetodo(pago.getPagoMetodo())
                .pagoReferencia(pago.getPagoReferencia())
                .pagoPeriodoInicio(pago.getPagoPeriodoInicio())
                .pagoPeriodoFin(pago.getPagoPeriodoFin())
                .pagoEstado(pago.getPagoEstado())
                .pagoNotas(pago.getPagoNotas())
                .auditoriaFechaCreacion(pago.getAuditoriaFechaCreacion())
                .build();
    }
}