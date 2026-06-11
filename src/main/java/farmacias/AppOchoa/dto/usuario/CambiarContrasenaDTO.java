package farmacias.AppOchoa.dto.usuario;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CambiarContrasenaDTO {

    @NotBlank(message = "La contraseña actual es obligatoria")
    private String contrasenaActual;

    @NotBlank(message = "La contraseña nueva es obligatoria")
    // Máximo 72: BCrypt ignora silenciosamente los bytes que pasan de ese límite
    @Size(min = 10, max = 72, message = "La contraseña debe tener entre 10 y 72 caracteres")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "La contraseña debe incluir al menos una mayúscula, una minúscula y un número")
    private String contrasenaNueva;
}
