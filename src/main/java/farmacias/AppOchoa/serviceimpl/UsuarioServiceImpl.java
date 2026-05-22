package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.usuario.*;
import farmacias.AppOchoa.model.Sucursal;
import farmacias.AppOchoa.model.Usuario;
import farmacias.AppOchoa.repository.SucursalRepository;
import farmacias.AppOchoa.repository.UsuarioRepository;
import farmacias.AppOchoa.exception.DuplicateResourceException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.services.UsuarioService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService, UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            SucursalRepository sucursalRepository,
            @Lazy PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.sucursalRepository = sucursalRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioResponseDTO crearUsuario(Long farmaciaId, UsuarioCreateDTO dto) {
        if (usuarioRepository.existsByNombreUsuarioUsuario(dto.getNombreUsuario())) {
            throw new DuplicateResourceException("El nombre de usuario '" + dto.getNombreUsuario() + "' ya está en uso");
        }

        Sucursal sucursal = null;
        if (dto.getSucursalId() != null) {
            sucursal = buscarSucursal(dto.getSucursalId());
        }

        Usuario usuario = Usuario.builder()
                .nombreUsuarioUsuario(dto.getNombreUsuario())
                .usuarioContrasenaHash(passwordEncoder.encode(dto.getContrasena()))
                .usuarioNombre(dto.getNombre())
                .usuarioApellido(dto.getApellido())
                .usuarioRol(dto.getRol())
                .usuarioEstado(true)
                .sucursal(sucursal)
                .build();

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(Long farmaciaId, Long id) {
        return usuarioRepository.findById(id)
                .map(UsuarioResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioSimpleDTO> listarUsuariosActivosPaginado(Long farmaciaId, Pageable pageable) {
        return usuarioRepository.findByUsuarioEstadoTrue(pageable)
                .map(UsuarioSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable){
        return usuarioRepository.buscarPorTexto(texto, pageable)
                .map(UsuarioSimpleDTO::fromEntity);
    }

    @Override
    public UsuarioResponseDTO actualizarUsuario(Long farmaciaId, Long id, UsuarioUpdateDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado ID: " + id));

        if (!usuario.getNombreUsuarioUsuario().equals(dto.getNombreUsuario())) {
            if (usuarioRepository.existsByNombreUsuarioUsuario(dto.getNombreUsuario())) {
                throw new DuplicateResourceException("El nuevo nombre de usuario ya está siendo utilizado por otra cuenta");
            }
            usuario.setNombreUsuarioUsuario(dto.getNombreUsuario());
        }

        if (dto.getSucursalId() != null) {
            Sucursal sucursal = buscarSucursal(dto.getSucursalId());
            usuario.setSucursal(sucursal);
        } else {
            usuario.setSucursal(null);
        }

        usuario.setUsuarioNombre(dto.getNombre());
        usuario.setUsuarioApellido(dto.getApellido());
        usuario.setUsuarioRol(dto.getRol());
        usuario.setUsuarioEstado(dto.getEstado());

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Override
    public void cambiarEstado(Long farmaciaId, Long id, Boolean nuevoEstado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado ID: " + id));
        usuario.setUsuarioEstado(nuevoEstado);
        usuarioRepository.save(usuario);
    }

    @Override
    public void eliminarUsuario(Long farmaciaId, Long id) {
        cambiarEstado(farmaciaId, id, false);
    }

    private Sucursal buscarSucursal(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada ID: " + id));
    }

    //UserDetailsService

    // Spring Security llama este método internamente en dos momentos:
    // 1. Durante el login — DaoAuthenticationProvider busca el usuario para comparar credenciales
    // 2. En cada request — JwtAuthenticationFilter lo llama para cargar el usuario desde la DB
    // Retorna el Usuario entity directamente porque implementa UserDetails
    // Si no existe lanza UsernameNotFoundException y Spring responde con 401 automáticamente
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByNombreUsuarioUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }
}