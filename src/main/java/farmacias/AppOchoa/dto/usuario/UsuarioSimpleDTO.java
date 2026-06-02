package farmacias.AppOchoa.dto.usuario;

import farmacias.AppOchoa.model.UsuarioRol;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioSimpleDTO {

    private Long usuarioId;

    private String nombreUsuario;

    private String nombreCompleto;

    private UsuarioRol rol;

    // Método helper
    public static UsuarioSimpleDTO fromEntity(farmacias.AppOchoa.model.Usuario usuario) {
        return UsuarioSimpleDTO.builder()
                .usuarioId(usuario.getUsuarioId())
                .nombreUsuario(usuario.getNombreUsuarioUsuario())
                .nombreCompleto(usuario.getUsuarioNombre() + " " + usuario.getUsuarioApellido())
                .rol(usuario.getUsuarioRol())
                .build();
    }
}