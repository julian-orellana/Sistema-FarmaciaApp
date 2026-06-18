package farmacias.AppOchoa.dto.venta;

import farmacias.AppOchoa.dto.ventadetalle.VentaDetalleCreateDTO;
import farmacias.AppOchoa.model.MetodoPagoEstado;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VentaCreateCobroDTO {

    //Datos de Venta
    @NotNull(message = "La sucursal es obligatoria")
    private Long sucursalId;
    @NotBlank(message = "El NIT del cliente es obligatorio")
    @Size(max = 20, message = "El NIT no debe exceder 20 caracteres")
    @Pattern(regexp = "^(CF|[0-9]{1,13}(-[0-9K])?)$",
            message = "NIT inválido (debe ser 'CF' o formato válido)")
    private String nitCliente;  // "CF" o "12345678-9"
    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(max = 150, message = "El nombre no debe exceder 150 caracteres")
    private String nombreCliente;
    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    @Digits(integer = 8, fraction = 2, message = "Formato inválido")
    private BigDecimal descuento;
    @NotEmpty(message = "Debe incluir al menos un producto")
    @Valid
    private List<VentaDetalleCreateDTO> detalles;

    //Datos de VentaPago
    @NotNull(message = "El ID de la caja es obligatorio")
    private Long cajaSesionId;
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto a recibido debe ser mayor que 0")
    private BigDecimal montoRecibido;
    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPagoEstado metodoPago;
    @Size(min = 5, max = 100, message = "La referencia debe tener entre 5 y 100 caracteres")
    private String referenciaTransaccion;



}
