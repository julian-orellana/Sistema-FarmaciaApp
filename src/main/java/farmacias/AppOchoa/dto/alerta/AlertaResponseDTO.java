package farmacias.AppOchoa.dto.alerta;

import farmacias.AppOchoa.dto.producto.ProductoSimpleDTO;
import farmacias.AppOchoa.dto.sucursal.SucursalSimpleDTO;
import farmacias.AppOchoa.model.Alerta;
import farmacias.AppOchoa.model.AlertaTipo;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertaResponseDTO {

    private Long alertaId;

    private AlertaTipo tipo;

    private String mensaje;

    private ProductoSimpleDTO producto;

    private SucursalSimpleDTO sucursal;

    private String loteNumero;

    private LocalDateTime fecha;

    private Boolean leida;

    public static AlertaResponseDTO fromEntity(Alerta alerta) {
        return AlertaResponseDTO.builder()
                .alertaId(alerta.getAlertaId())
                .tipo(alerta.getAlertaTipo())
                .mensaje(alerta.getAlertaMensaje())
                .producto(alerta.getProducto() != null ?
                        ProductoSimpleDTO.fromEntity(alerta.getProducto()) : null)
                .sucursal(alerta.getSucursal() != null ?
                        SucursalSimpleDTO.fromEntity(alerta.getSucursal()) : null)
                .loteNumero(alerta.getLote() != null ?
                        alerta.getLote().getLoteNumero() : null)
                .fecha(alerta.getAlertaFecha())
                .leida(alerta.getAlertaLeida())
                .build();
    }
}