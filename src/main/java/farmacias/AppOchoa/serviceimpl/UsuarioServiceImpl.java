package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.usuario.*;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.Sucursal;
import farmacias.AppOchoa.model.Usuario;
import farmacias.AppOchoa.model.UsuarioRol;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.repository.SucursalRepository;
import farmacias.AppOchoa.repository.UsuarioRepository;
import farmacias.AppOchoa.exception.BadRequestException;
import farmacias.AppOchoa.exception.DuplicateResourceException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.services.RefreshTokenService;
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
    private final FarmaciaRepository farmaciaRepository;
    private final RefreshTokenService refreshTokenService;

    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            SucursalRepository sucursalRepository,
            FarmaciaRepository farmaciaRepository,
            @Lazy PasswordEncoder passwordEncoder,
            RefreshTokenService refreshTokenService) {
        this.usuarioRepository = usuarioRepository;
        this.sucursalRepository = sucursalRepository;
        this.farmaciaRepository = farmaciaRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public UsuarioResponseDTO crearUsuario(Long farmaciaId, UsuarioCreateDTO dto) {
        if (usuarioRepository.existsByNombreUsuarioUsuario(dto.getNombreUsuario())) {
            throw new DuplicateResourceException("El nombre de usuario '" + dto.getNombreUsuario() + "' ya está en uso");
        }

        if (dto.getRol() == UsuarioRol.superadmin) {
            throw new BadRequestException("No se puede asignar el rol superadmin");
        }

        Farmacia farmacia = farmaciaRepository.getReferenceById(farmaciaId);

        validarCupoUsuarios(farmaciaId, farmacia.getMaxUsuarios());

        Sucursal sucursal = null;
        if (dto.getSucursalId() != null) {
            sucursal = buscarSucursal(farmaciaId, dto.getSucursalId());
        }

        Usuario usuario = Usuario.builder()
                .nombreUsuarioUsuario(dto.getNombreUsuario())
                .usuarioContrasenaHash(passwordEncoder.encode(dto.getContrasena()))
                .usuarioNombre(dto.getNombre())
                .usuarioApellido(dto.getApellido())
                .usuarioRol(dto.getRol())
                .usuarioEstado(true)
                .sucursal(sucursal)
                .farmacia(farmacia)
                .build();

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(Long farmaciaId, Long id) {
        return usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .map(UsuarioResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioSimpleDTO> listarUsuariosActivosPaginado(Long farmaciaId, Pageable pageable) {
        return usuarioRepository.findByFarmacia_FarmaciaIdAndUsuarioEstadoTrue(farmaciaId, pageable)
                .map(UsuarioSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable){
        return usuarioRepository.buscarPorTexto(farmaciaId, texto, pageable)
                .map(UsuarioSimpleDTO::fromEntity);
    }

    @Override
    public UsuarioResponseDTO actualizarUsuario(Long farmaciaId, Long id, UsuarioUpdateDTO dto) {
        Usuario usuario = usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado ID: " + id));

        if (!usuario.getNombreUsuarioUsuario().equals(dto.getNombreUsuario())) {
            if (usuarioRepository.existsByNombreUsuarioUsuario(dto.getNombreUsuario())) {
                throw new DuplicateResourceException("El nuevo nombre de usuario ya está siendo utilizado por otra cuenta");
            }
            usuario.setNombreUsuarioUsuario(dto.getNombreUsuario());
        }

        if (dto.getRol() == UsuarioRol.superadmin) {
            throw new BadRequestException("No se puede asignar el rol superadmin");
        }

        if (dto.getSucursalId() != null) {
            Sucursal sucursal = buscarSucursal(farmaciaId, dto.getSucursalId());
            usuario.setSucursal(sucursal);
        } else {
            usuario.setSucursal(null);
        }

        if (Boolean.TRUE.equals(dto.getEstado()) && !Boolean.TRUE.equals(usuario.getUsuarioEstado())) {
            validarCupoUsuarios(farmaciaId, usuario.getFarmacia().getMaxUsuarios());
        }

        usuario.setUsuarioNombre(dto.getNombre());
        usuario.setUsuarioApellido(dto.getApellido());
        usuario.setUsuarioRol(dto.getRol());
        usuario.setUsuarioEstado(dto.getEstado());

        UsuarioResponseDTO respuesta = UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));

        if (Boolean.FALSE.equals(dto.getEstado())) {
            refreshTokenService.revocarPorUsuario(usuario.getUsuarioId());
        }

        return respuesta;
    }

    @Override
    public void cambiarEstado(Long farmaciaId, Long id, Boolean nuevoEstado) {
        Usuario usuario = usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado ID: " + id));

        if (Boolean.TRUE.equals(nuevoEstado) && !Boolean.TRUE.equals(usuario.getUsuarioEstado())) {
            validarCupoUsuarios(farmaciaId, usuario.getFarmacia().getMaxUsuarios());
        }

        usuario.setUsuarioEstado(nuevoEstado);
        usuarioRepository.save(usuario);

        if (Boolean.FALSE.equals(nuevoEstado)) {
            refreshTokenService.revocarPorUsuario(usuario.getUsuarioId());
        }
    }

    @Override
    public void eliminarUsuario(Long farmaciaId, Long id) {
        cambiarEstado(farmaciaId, id, false);
    }

    @Override
    public void cambiarContrasena(Long usuarioId, CambiarContrasenaDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado ID: " + usuarioId));

        if (!passwordEncoder.matches(dto.getContrasenaActual(), usuario.getUsuarioContrasenaHash())) {
            throw new BadRequestException("La contraseña actual es incorrecta");
        }

        usuario.setUsuarioContrasenaHash(passwordEncoder.encode(dto.getContrasenaNueva()));
        usuarioRepository.save(usuario);

        refreshTokenService.revocarPorUsuario(usuarioId);
    }

    private void validarCupoUsuarios(Long farmaciaId, Integer maxUsuarios) {
        if (maxUsuarios == null) {
            return;
        }
        long activos = usuarioRepository.countByFarmacia_FarmaciaIdAndUsuarioEstadoTrue(farmaciaId);
        if (activos >= maxUsuarios) {
            throw new BadRequestException("Tu plan permite máximo " + maxUsuarios + " usuarios activos");
        }
    }

    private Sucursal buscarSucursal(Long farmaciaId, Long id) {
        return sucursalRepository.findBySucursalIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada en tu farmacia ID: " + id));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByNombreUsuarioUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }
}