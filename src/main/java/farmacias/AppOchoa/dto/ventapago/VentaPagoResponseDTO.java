package farmacias.AppOchoa.dto.ventapago;


import farmacias.AppOchoa.dto.cajasesiones.CajaSesionesSimpleDTO;
import farmacias.AppOchoa.dto.venta.VentaSimpleDTO;
import farmacias.AppOchoa.model.MetodoPagoEstado;
import farmacias.AppOchoa.model.VentaPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VentaPagoResponseDTO {
    private Long pagoId;
    private VentaSimpleDTO venta;
    private CajaSesionesSimpleDTO caja;
    private MetodoPagoEstado metodoPagoEstado;
    private BigDecimal montoRecibido;
    private BigDecimal montoVuelto;
    private String referenciaTransaccion;
    private LocalDateTime auditoriaFechaCreacion;

    public static VentaPagoResponseDTO fromEntity(VentaPago ventaPago){
        return VentaPagoResponseDTO.builder()
                .pagoId(ventaPago.getPagoId())
                .venta(ventaPago.getVenta() != null ?
                        VentaSimpleDTO.fromEntity(ventaPago.getVenta()): null)
                .caja(ventaPago.getCajaSesiones() != null ?
                        CajaSesionesSimpleDTO.fromEntity(ventaPago.getCajaSesiones()): null)
                .metodoPagoEstado(ventaPago.getMetodoPago())
                .montoRecibido(ventaPago.getMontoRecibido())
                .montoVuelto(ventaPago.getMontoVuelto())
                .referenciaTransaccion(ventaPago.getReferenciaTransaccion())
                .auditoriaFechaCreacion(ventaPago.getAuditoriaFechaCreacion())
                .build();
    }
}
