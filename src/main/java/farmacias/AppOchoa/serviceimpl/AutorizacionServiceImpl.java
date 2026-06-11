package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.autorizacion.AutorizacionCreateDTO;
import farmacias.AppOchoa.dto.autorizacion.AutorizacionResponseDTO;
import farmacias.AppOchoa.dto.autorizacion.AutorizacionSimpleDTO;
import farmacias.AppOchoa.exception.BadRequestException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.Autorizacion;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.Usuario;
import farmacias.AppOchoa.model.UsuarioRol;
import farmacias.AppOchoa.repository.AutorizacionRepository;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.repository.UsuarioRepository;
import farmacias.AppOchoa.services.AutorizacionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AutorizacionServiceImpl implements AutorizacionService {
    private final AutorizacionRepository autorizacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final FarmaciaRepository farmaciaRepository;

    public AutorizacionServiceImpl(
            AutorizacionRepository autorizacionRepository,
            UsuarioRepository usuarioRepository,
            FarmaciaRepository farmaciaRepository){
        this.autorizacionRepository = autorizacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.farmaciaRepository = farmaciaRepository;
    }
    @Override
    public AutorizacionResponseDTO crear(Long farmaciaId, AutorizacionCreateDTO dto){
        Usuario usuario = buscarCajero(farmaciaId, dto.getCajeroId());
        Usuario supervisor = buscarSupervisor(farmaciaId, dto.getSupervisorId());
        Farmacia farmacia = farmaciaRepository.getReferenceById(farmaciaId);

        Autorizacion autorizacion = Autorizacion.builder()
                .autorizacionReferenciaId(dto.getAutorizacionReferenciaId())
                .cajero(usuario)
                .supervisor(supervisor)
                .autorizacionTipo(dto.getAutorizacionTipo())
                .farmacia(farmacia)
                .build();
        return AutorizacionResponseDTO.fromEntity(autorizacionRepository.save(autorizacion));
    }

    // Auxiliares
    private Usuario buscarCajero(Long farmaciaId, Long id){
        if(id == null) return null;
        return usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cajero no encontrado en tu farmacia"));
    }

    // Scopeado por farmacia y restringido por rol: un supervisor de otra farmacia
    // o un vendedor no puede autorizar operaciones (M10). superadmin tampoco:
    // es operador de plataforma, no personal de la farmacia.
    private Usuario buscarSupervisor(Long farmaciaId, Long id){
        if(id == null) return null;
        Usuario supervisor = usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Supervisor no encontrado en tu farmacia"));

        if (supervisor.getUsuarioRol() != UsuarioRol.administrador
                && supervisor.getUsuarioRol() != UsuarioRol.encargado) {
            throw new BadRequestException("El supervisor debe tener rol administrador o encargado");
        }
        return supervisor;
    }

    @Override
    @Transactional(readOnly = true)
    public AutorizacionResponseDTO buscarPorId(Long farmaciaId, Long id){
        return autorizacionRepository.findByAutorizacionIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .map(AutorizacionResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Autorizacion no encontrada por ID"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AutorizacionSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable) {
        return autorizacionRepository.buscarPorTexto(farmaciaId, texto, pageable)
                .map(AutorizacionSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AutorizacionSimpleDTO> listarTodas(Long farmaciaId, Pageable pageable){
        return autorizacionRepository.findByFarmacia_FarmaciaId(farmaciaId, pageable)
                .map(AutorizacionSimpleDTO::fromEntity);
    }

    @Override
    public void eliminar(Long farmaciaId, Long id) {
        throw new UnsupportedOperationException("Por reglas de auditoría financiera, este registro es histórico y no puede ser eliminado ni modificado.");
    }

    }


