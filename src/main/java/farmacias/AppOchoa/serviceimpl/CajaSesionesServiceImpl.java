package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.cajasesiones.CajaSesionesCreateDTO;
import farmacias.AppOchoa.dto.cajasesiones.CajaSesionesResponseDTO;
import farmacias.AppOchoa.dto.cajasesiones.CajaSesionesSimpleDTO;
import farmacias.AppOchoa.exception.BadRequestException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.Caja;
import farmacias.AppOchoa.model.CajaSesiones;
import farmacias.AppOchoa.model.SesionEstado;
import farmacias.AppOchoa.model.Sucursal;
import farmacias.AppOchoa.model.Usuario;
import farmacias.AppOchoa.repository.CajaRepository;
import farmacias.AppOchoa.repository.CajaSesionesRepository;
import farmacias.AppOchoa.repository.SucursalRepository;
import farmacias.AppOchoa.repository.UsuarioRepository;
import farmacias.AppOchoa.services.CajaSesionesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class CajaSesionesServiceImpl implements CajaSesionesService {
    private final CajaSesionesRepository cajaSesionesRepository;
    private final UsuarioRepository usuarioRepository;
    private final CajaRepository cajaRepository;
    private final SucursalRepository sucursalRepository;

    public CajaSesionesServiceImpl(
            CajaSesionesRepository cajaSesionesRepository,
            UsuarioRepository usuarioRepository,
            CajaRepository cajaRepository,
            SucursalRepository sucursalRepository){
        this.cajaSesionesRepository = cajaSesionesRepository;
        this.usuarioRepository = usuarioRepository;
        this.cajaRepository = cajaRepository;
        this.sucursalRepository = sucursalRepository;
    }

    private Sucursal buscarSucursal(Long farmaciaId){
        return sucursalRepository.findByFarmacia_FarmaciaId(farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada para tu farmacia"));
    }

    @Override
    public CajaSesionesResponseDTO crear(Long farmaciaId, CajaSesionesCreateDTO dto){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        // La sesion la abre quien esta autenticado, nunca un id del request (M4)
        Usuario solicitante = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = buscarUsuario(farmaciaId, solicitante.getUsuarioId());
        Caja caja = buscarCaja(sucursal.getSucursalId(), dto.getCajaId());

        CajaSesiones cajaSesiones = CajaSesiones.builder()
                .caja(caja)
                .usuario(usuario)
                .sesionFondoInicial(dto.getSesionFondoInicial())
                .sucursal(sucursal)

                .build();

        return CajaSesionesResponseDTO.fromEntity(cajaSesionesRepository.save(cajaSesiones));

    }
    @Override
    public CajaSesionesResponseDTO cerrar(Long farmaciaId, Long id){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        CajaSesiones sesion = cajaSesionesRepository.findBySesionIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .orElseThrow(()-> new ResourceNotFoundException("Sesion no encontrada por ID"));

        // No re-cerrar: pisaria la fecha de cierre original (M7)
        if (sesion.getSesionEstado() == SesionEstado.cerrada) {
            throw new BadRequestException("La sesión ya está cerrada");
        }

        sesion.setSesionFechaCierre(LocalDateTime.now());
        sesion.setSesionEstado(SesionEstado.cerrada);

        return CajaSesionesResponseDTO.fromEntity(cajaSesionesRepository.save(sesion));
    }
    //Metodos Auxiliares
    private Usuario buscarUsuario(Long farmaciaId, Long id){
        if(id == null) return null;
        return usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado en tu farmacia"));
    }
    private Caja buscarCaja(Long sucursalId, Long id){
        if(id == null) return null;
        return cajaRepository.findByCajaIdAndSucursal_SucursalId(id, sucursalId)
                .orElseThrow(()-> new ResourceNotFoundException("Caja no encontrada en tu farmacia"));
    }
    @Transactional(readOnly = true)
    @Override
    public CajaSesionesResponseDTO buscarPorId(Long farmaciaId, Long id){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return cajaSesionesRepository.findBySesionIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .map(CajaSesionesResponseDTO::fromEntity)
                .orElseThrow(()-> new ResourceNotFoundException("Sesion no encontrada por ID"));
    }
    @Override
    @Transactional(readOnly = true)
    public Page<CajaSesionesSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return cajaSesionesRepository.buscarPorTexto(sucursal.getSucursalId(), texto, pageable)
                .map(CajaSesionesSimpleDTO::fromEntity);
    }
    @Transactional(readOnly = true)
    @Override
    public Page<CajaSesionesSimpleDTO> listarSesiones(Long farmaciaId,Pageable pageable){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return cajaSesionesRepository.findBySucursal_SucursalId(sucursal.getSucursalId(), pageable)
                .map(CajaSesionesSimpleDTO::fromEntity);
    }
    @Override
    public void eliminar(Long farmaciaId, Long id) {
        throw new UnsupportedOperationException("Por reglas de auditoría financiera, este registro es histórico y no puede ser eliminado ni modificado.");
    }
}






