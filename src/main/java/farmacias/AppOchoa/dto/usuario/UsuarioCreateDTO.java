package farmacias.AppOchoa.dto.usuario;

import farmacias.AppOchoa.model.UsuarioRol;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UsuarioCreateDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
    private String nombreUsuario;

    @NotBlank(message = "La contraseña es obligatoria")
    // Máximo 72: BCrypt ignora silenciosamente los bytes que pasan de ese límite
    @Size(min = 10, max = 72, message = "La contraseña debe tener entre 10 y 72 caracteres")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "La contraseña debe incluir al menos una mayúscula, una minúscula y un número")
    private String contrasena;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    private String apellido;

    @NotNull(message = "El rol es obligatorio")
    private UsuarioRol rol;

    @NotNull(message = "La sucursal es obligatoria")
    private Long sucursalId;
}