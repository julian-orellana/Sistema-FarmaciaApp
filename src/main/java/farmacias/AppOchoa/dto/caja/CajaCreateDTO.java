package farmacias.AppOchoa.dto.caja;

import farmacias.AppOchoa.model.CajaEstado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CajaCreateDTO {
    @NotBlank(message = "El nombre de la caja es obligatorio")
    @Size(min = 5, max = 100, message = "El nombre debe contener entre 5 y 100 caracteres")
    private String cajaNombre;
    @NotNull(message = "La sucursal es obligatoria")
    private Long sucursalId;
}
