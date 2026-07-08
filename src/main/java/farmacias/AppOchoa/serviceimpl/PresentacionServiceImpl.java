package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.presentacion.PresentacionCreateDTO;
import farmacias.AppOchoa.dto.presentacion.PresentacionResponseDTO;
import farmacias.AppOchoa.dto.presentacion.PresentacionSimpleDTO;
import farmacias.AppOchoa.dto.presentacion.PresentacionUpdateDTO;
import farmacias.AppOchoa.model.Presentacion;
import farmacias.AppOchoa.model.Sucursal;
import farmacias.AppOchoa.repository.PresentacionRepository;
import farmacias.AppOchoa.repository.SucursalRepository;
import farmacias.AppOchoa.exception.DuplicateResourceException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.services.PresentacionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PresentacionServiceImpl implements PresentacionService {

    private final PresentacionRepository presentacionRepository;
    private final SucursalRepository sucursalRepository;

    public PresentacionServiceImpl(
            PresentacionRepository presentacionRepository,
            SucursalRepository sucursalRepository){
        this.presentacionRepository = presentacionRepository;
        this.sucursalRepository = sucursalRepository;
    }

    //Método auxiliar
    private Sucursal buscarSucursal(Long farmaciaId){
        return sucursalRepository.findByFarmacia_FarmaciaId(farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada para tu farmacia"));
    }

    @Override
    public PresentacionResponseDTO crear(Long farmaciaId, PresentacionCreateDTO dto){
        Sucursal sucursal = buscarSucursal(farmaciaId);

        if(presentacionRepository.existsBySucursal_SucursalIdAndPresentacionNombre(sucursal.getSucursalId(), dto.getNombre())){
            throw new DuplicateResourceException("Ya existe una presentación con ese nombre: " + dto.getNombre());
        }

        Presentacion presentacion = Presentacion.builder()
                .presentacionNombre(dto.getNombre())
                .presentacionEstado(true)
                .farmacia(sucursal.getFarmacia())
                .sucursal(sucursal)
                .build();

        return PresentacionResponseDTO.fromEntity(presentacionRepository.save(presentacion));
    }

    @Override
    @Transactional(readOnly = true)
    public PresentacionResponseDTO obtenerPorId(Long farmaciaId, Long id){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        Presentacion presentacion = presentacionRepository.findByPresentacionIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Presentación no encontrada por ID: " + id));
        return PresentacionResponseDTO.fromEntity(presentacion);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<PresentacionSimpleDTO> listarActivasPaginadas(Long farmaciaId, Pageable pageable) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return presentacionRepository.findBySucursal_SucursalIdAndPresentacionEstadoTrue(sucursal.getSucursalId(), pageable)
                .map(PresentacionSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PresentacionSimpleDTO> listarTodasPaginadas(Long farmaciaId, Pageable pageable) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return presentacionRepository.findBySucursal_SucursalId(sucursal.getSucursalId(), pageable)
                .map(PresentacionSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PresentacionSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return presentacionRepository.buscarPorTexto(sucursal.getSucursalId(), texto, pageable)
                .map(PresentacionSimpleDTO::fromEntity);
    }

    @Override
    public PresentacionResponseDTO actualizar(Long farmaciaId, Long id, PresentacionUpdateDTO dto){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        Presentacion presentacion = presentacionRepository.findByPresentacionIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Presentación no encontrada por ID: " + id));

        if(!presentacion.getPresentacionNombre().equalsIgnoreCase(dto.getNombre())){
            if(presentacionRepository.existsBySucursal_SucursalIdAndPresentacionNombre(sucursal.getSucursalId(), dto.getNombre())){
                throw new DuplicateResourceException("Ya existe otra presentación con el nombre: " + dto.getNombre());
            }
        }

        presentacion.setPresentacionNombre(dto.getNombre());

        if (dto.getEstado() != null) {
            presentacion.setPresentacionEstado(dto.getEstado());
        }

        return PresentacionResponseDTO.fromEntity(presentacionRepository.save(presentacion));
    }

    @Override
    public void cambiarEstado(Long farmaciaId, Long id, Boolean estado){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        Presentacion presentacion = presentacionRepository.findByPresentacionIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Presentación no encontrada por ID: " + id));
        presentacion.setPresentacionEstado(estado);
        presentacionRepository.save(presentacion);
    }

    @Override
    public void eliminar(Long farmaciaId, Long id){
        cambiarEstado(farmaciaId, id, false);
    }
}
