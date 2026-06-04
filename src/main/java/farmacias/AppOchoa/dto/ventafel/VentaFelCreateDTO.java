package farmacias.AppOchoa.dto.ventafel;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VentaFelCreateDTO {

    @NotNull(message = "El ID de la venta es obligatorio")
    private Long ventaId;
}