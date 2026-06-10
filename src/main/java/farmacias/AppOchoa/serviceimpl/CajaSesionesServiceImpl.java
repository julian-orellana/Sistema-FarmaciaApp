package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.cajasesiones.CajaSesionesCreateDTO;
import farmacias.AppOchoa.dto.cajasesiones.CajaSesionesResponseDTO;
import farmacias.AppOchoa.dto.cajasesiones.CajaSesionesSimpleDTO;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.Caja;
import farmacias.AppOchoa.model.CajaSesiones;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.Usuario;
import farmacias.AppOchoa.repository.CajaRepository;
import farmacias.AppOchoa.repository.CajaSesionesRepository;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.repository.UsuarioRepository;
import farmacias.AppOchoa.services.CajaSesionesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CajaSesionesServiceImpl implements CajaSesionesService {
    private final CajaSesionesRepository cajaSesionesRepository;
    private final UsuarioRepository usuarioRepository;
    private final CajaRepository cajaRepository;
    private final FarmaciaRepository farmaciaRepository;

    public CajaSesionesServiceImpl(
            CajaSesionesRepository cajaSesionesRepository,
            UsuarioRepository usuarioRepository,
            CajaRepository cajaRepository,
            FarmaciaRepository farmaciaRepository){
        this.cajaSesionesRepository = cajaSesionesRepository;
        this.usuarioRepository = usuarioRepository;
        this.cajaRepository = cajaRepository;
        this.farmaciaRepository = farmaciaRepository;
    }
    @Override
    public CajaSesionesResponseDTO crear(Long farmaciaId, CajaSesionesCreateDTO dto){
        Usuario usuario = buscarUsuario(farmaciaId, dto.getUsuarioId());
        Caja caja = buscarCaja(farmaciaId, dto.getCajaId());
        Farmacia farmacia = farmaciaRepository.getReferenceById(farmaciaId);

        CajaSesiones cajaSesiones = CajaSesiones.builder()
                .caja(caja)
                .usuario(usuario)
                .sesionFondoInicial(dto.getSesionFondoInicial())
                .farmacia(farmacia)
                .build();

        return CajaSesionesResponseDTO.fromEntity(cajaSesionesRepository.save(cajaSesiones));

    }
    //Metodos Auxiliares
    private Usuario buscarUsuario(Long farmaciaId, Long id){
        if(id == null) return null;
        return usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado en tu farmacia"));
    }
    private Caja buscarCaja(Long farmaciaId, Long id){
        if(id == null) return null;
        return cajaRepository.findByCajaIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(()-> new ResourceNotFoundException("Caja no encontrada en tu farmacia"));
    }
    @Transactional(readOnly = true)
    @Override
    public CajaSesionesResponseDTO buscarPorId(Long farmaciaId, Long id){
        return cajaSesionesRepository.findBySesionIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .map(CajaSesionesResponseDTO::fromEntity)
                .orElseThrow(()-> new ResourceNotFoundException("Sesion no encontrada por ID"));
    }
    @Override
    @Transactional(readOnly = true)
    public Page<CajaSesionesSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable) {
        return cajaSesionesRepository.buscarPorTexto(farmaciaId, texto, pageable)
                .map(CajaSesionesSimpleDTO::fromEntity);
    }
    @Transactional(readOnly = true)
    @Override
    public Page<CajaSesionesSimpleDTO> listarSesiones(Long farmaciaId,Pageable pageable){
        return cajaSesionesRepository.findByFarmacia_FarmaciaId(farmaciaId, pageable)
                .map(CajaSesionesSimpleDTO::fromEntity);
    }
    @Override
    public void eliminar(Long farmaciaId, Long id) {
        throw new UnsupportedOperationException("Por reglas de auditoría financiera, este registro es histórico y no puede ser eliminado ni modificado.");
    }
}






