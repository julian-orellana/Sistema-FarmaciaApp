package farmacias.AppOchoa.dto.usuario;

import farmacias.AppOchoa.dto.sucursal.SucursalSimpleDTO;
import farmacias.AppOchoa.model.UsuarioRol;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {

    private Long usuarioId;
    private String nombreUsuario;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private UsuarioRol rol;
    private Boolean estado;
    private LocalDateTime fechaCreacion;
    private SucursalSimpleDTO sucursal;

    // Método helper para construir nombreCompleto
    public static UsuarioResponseDTO fromEntity(farmacias.AppOchoa.model.Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .usuarioId(usuario.getUsuarioId())
                .nombreUsuario(usuario.getNombreUsuarioUsuario())
                .nombre(usuario.getUsuarioNombre())
                .apellido(usuario.getUsuarioApellido())
                .nombreCompleto(usuario.getUsuarioNombre() + " " + usuario.getUsuarioApellido())
                .rol(usuario.getUsuarioRol())
                .estado(usuario.getUsuarioEstado())
                .fechaCreacion(usuario.getAuditoriaFechaCreacion())
                .sucursal(usuario.getSucursal() != null ?
                        SucursalSimpleDTO.fromEntity(usuario.getSucursal()) : null)
                .build();
    }
}