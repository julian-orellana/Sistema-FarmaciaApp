package farmacias.AppOchoa.dto.ventafelnotascredito;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VentaFelNotasCreditoCreateDTO {

    @NotNull(message = "El ID del FEL es obligatorio")
    private Long felId;

    @NotBlank(message = "El motivo de la nota de crédito es obligatorio")
    private String notaMotivo;
}