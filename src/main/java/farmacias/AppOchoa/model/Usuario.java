package farmacias.AppOchoa.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "usuario_nombre_usuario", nullable = false, length = 50)
    private String nombreUsuarioUsuario;

    @Column(name = "usuario_contrasena_hash", nullable = false, length = 255)
    private String usuarioContrasenaHash;

    @Column(name = "usuario_nombre", nullable = false, length = 100)
    private String usuarioNombre;

    @Column(name = "usuario_apellido", nullable = false, length = 100)
    private String usuarioApellido;

    @Enumerated(EnumType.STRING)
    @Column(name = "usuario_rol", nullable = false, length = 20)
    private UsuarioRol usuarioRol;

    @Column(name = "usuario_estado", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean usuarioEstado = true;

    @Column(name = "auditoria_fecha_creacion", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime auditoriaFechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmacia_id", nullable = false)
    private Farmacia farmacia;

    // UserDetails

    // Retorna los roles del usuario con prefijo ROLE_ requerido por Spring Security
    // para que hasRole() funcione en SecurityConfig y @PreAuthorize
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(usuarioRol.name()));
    }

    // Retorna la contraseña hasheada almacenada en DB
    // DaoAuthenticationProvider la compara contra la contraseña en texto plano usando BCrypt
    @Override
    public String getPassword() {
        return usuarioContrasenaHash;
    }

    // Retorna el identificador único del usuario para Spring Security
    // Se usa en loadUserByUsername() y como subject del JWT
    @Override
    public String getUsername() {
        return nombreUsuarioUsuario;
    }

    // AppOchoa no maneja expiración de cuentas por tiempo
    @Override
    public boolean isAccountNonExpired() { return true; }

    // AppOchoa no maneja bloqueo por intentos fallidos
    @Override
    public boolean isAccountNonLocked() { return true; }

    // AppOchoa no maneja expiración de contraseñas
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    // Mapea usuarioEstado de la DB a Spring Security
    // Si false, Spring rechaza el login automáticamente con DisabledException
    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(usuarioEstado);
    }
}