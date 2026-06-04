package farmacias.AppOchoa.dto.sucursal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SucursalUpdateDTO {

    @NotBlank(message = "El nombre de la sucursal es obligatorio")
    @Size(max = 100, message = "El nombre no debe tener mas de 100 caracteres")
    private String nombre;

    @NotBlank(message = "La direccion de la sucursal es obligatoria")
    @Size(max = 200, message = "La direccion no debe tener mas de 200 caracteres")
    private String direccion;

    @Size(max = 20, message = "El telefono de la sucursal no debe de tener mas de 20 caracteres")
    private String telefono;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;

}
