package farmacias.AppOchoa.dto.usuario;

import farmacias.AppOchoa.model.UsuarioRol;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioUpdateDTO {

    // NUEVO: Agregado para poder validar o mantener el username
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 50, message = "El nombre de usuario no puede exceder 50 caracteres")
    private String nombreUsuario;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    private String apellido;

    @NotNull(message = "El rol es obligatorio")
    private UsuarioRol rol;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;

    // Nota: En el Service lo pusimos opcional para Admins,
    // pero si en tu negocio siempre deben tener sucursal, mantén el @NotNull.
    private Long sucursalId;
}