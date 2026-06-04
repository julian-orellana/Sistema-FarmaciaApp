package farmacias.AppOchoa.dto.alerta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertaCreateDTO {

    @NotBlank(message = "El mensaje de la alerta es obligatorio")
    @Size(max = 255, message = "El mensaje no puede exceder los 255 caracteres")
    private String mensaje;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    @NotNull(message = "El ID de la sucursal es obligatorio")
    private Long sucursalId;

    // El lote es opcional, ya que algunas alertas pueden ser generales de stock por producto
    private Long loteId;
}