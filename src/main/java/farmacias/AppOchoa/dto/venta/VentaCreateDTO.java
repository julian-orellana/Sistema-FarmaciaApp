package farmacias.AppOchoa.dto.venta;

import farmacias.AppOchoa.dto.ventadetalle.VentaDetalleCreateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VentaCreateDTO {

    @NotNull(message = "La sucursal es obligatoria")
    private Long sucursalId;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;


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
}