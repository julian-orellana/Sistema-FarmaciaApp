package farmacias.AppOchoa.dto.farmacia;

import farmacias.AppOchoa.model.PlanTipo;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FarmaciaCreateDTO {

    @NotBlank(message = "El nombre de la farmacia es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String farmaciaNombre;

    @NotBlank(message = "El NIT de la farmacia es obligatorio")
    @Size(max = 9, message = "El NIT no debe exceder 9 caracteres")
    private String farmaciaNit;

    @NotBlank(message = "El email de la farmacia es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    @Size(max = 100, message = "El email no debe exceder 100 caracteres")
    private String farmaciaEmail;

    @NotBlank(message = "El teléfono de la farmacia es obligatorio")
    @Size(max = 20, message = "El teléfono no debe exceder 20 caracteres")
    private String farmaciaTelefono;

    @NotNull(message = "El plan es obligatorio")
    private PlanTipo planTipo;

    private LocalDate pruebaHasta;
    private LocalDate suscripcionVigencia;
}