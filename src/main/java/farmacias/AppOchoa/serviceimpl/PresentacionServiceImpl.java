package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.presentacion.PresentacionCreateDTO;
import farmacias.AppOchoa.dto.presentacion.PresentacionResponseDTO;
import farmacias.AppOchoa.dto.presentacion.PresentacionSimpleDTO;
import farmacias.AppOchoa.dto.presentacion.PresentacionUpdateDTO;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.Presentacion;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.repository.PresentacionRepository;
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
    private final FarmaciaRepository farmaciaRepository;

    public PresentacionServiceImpl(
            PresentacionRepository presentacionRepository,
            FarmaciaRepository farmaciaRepository){
        this.presentacionRepository = presentacionRepository;
        this.farmaciaRepository = farmaciaRepository;
    }

    @Override
    public PresentacionResponseDTO crear(Long farmaciaId, PresentacionCreateDTO dto){
        if(presentacionRepository.existsByFarmacia_FarmaciaIdAndPresentacionNombre(farmaciaId, dto.getNombre())){
            throw new DuplicateResourceException("Ya existe una presentación con ese nombre: " + dto.getNombre());
        }
        Farmacia farmacia = farmaciaRepository.getReferenceById(farmaciaId);

        Presentacion presentacion = Presentacion.builder()
                .presentacionNombre(dto.getNombre())
                .presentacionEstado(true)
                .farmacia(farmacia)
                .build();

        return PresentacionResponseDTO.fromEntity(presentacionRepository.save(presentacion));
    }

    @Override
    public PresentacionResponseDTO obtenerPorId(Long farmaciaId, Long id){
        Presentacion presentacion = presentacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presentación no encontrada por ID: " + id));
        return PresentacionResponseDTO.fromEntity(presentacion);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<PresentacionSimpleDTO> listarActivasPaginadas(Long farmaciaId, Pageable pageable) {
        return presentacionRepository.findByPresentacionEstadoTrue(pageable)
                .map(PresentacionSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PresentacionSimpleDTO> listarTodasPaginadas(Long farmaciaId, Pageable pageable) {
        return presentacionRepository.findAll(pageable)
                .map(PresentacionSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PresentacionSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable){
        return presentacionRepository.buscarPorTexto(texto, pageable)
                .map(PresentacionSimpleDTO::fromEntity);
    }

    @Override
    public PresentacionResponseDTO actualizar(Long farmaciaId, Long id, PresentacionUpdateDTO dto){
        Presentacion presentacion = presentacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presentación no encontrada por ID: " + id));

        if(!presentacion.getPresentacionNombre().equalsIgnoreCase(dto.getNombre())){
            if(presentacionRepository.existsByFarmacia_FarmaciaIdAndPresentacionNombre(farmaciaId, dto.getNombre())){
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
        Presentacion presentacion = presentacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presentación no encontrada por ID: " + id));
        presentacion.setPresentacionEstado(estado);
        presentacionRepository.save(presentacion);
    }

    @Override
    public void eliminar(Long farmaciaId, Long id){
        cambiarEstado(farmaciaId, id, false);
    }
}