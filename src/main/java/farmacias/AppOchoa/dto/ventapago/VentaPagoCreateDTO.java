package farmacias.AppOchoa.dto.ventapago;

import farmacias.AppOchoa.model.MetodoPagoEstado;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VentaPagoCreateDTO {
    @NotNull(message = "El ID de la venta es obligatorio")
    private Long ventaId;
    @NotNull(message = "El ID de la caja es obligatorio")
    private Long cajaSesionId;
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto a recibido debe ser mayor que 0")
    private BigDecimal montoRecibido;
    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPagoEstado metodoPago;
    @Size(min = 5, max = 100, message = "La referencia debe tener entre 5 y 100 caracteres")
    private String referenciaTransaccion;
    private BigDecimal montoVuelto;
}
