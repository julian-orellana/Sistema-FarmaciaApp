package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.sucursal.SucursalCreateDTO;
import farmacias.AppOchoa.dto.sucursal.SucursalResponseDTO;
import farmacias.AppOchoa.dto.sucursal.SucursalSimpleDTO;
import farmacias.AppOchoa.dto.sucursal.SucursalUpdateDTO;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.Sucursal;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.repository.SucursalRepository;
import farmacias.AppOchoa.exception.DuplicateResourceException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.services.SucursalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SucursalServiceImpl implements SucursalService {

    private final SucursalRepository sucursalRepository;
    private final FarmaciaRepository farmaciaRepository;

    public SucursalServiceImpl(SucursalRepository sucursalRepository, FarmaciaRepository farmaciaRepository){
        this.sucursalRepository = sucursalRepository;
        this.farmaciaRepository = farmaciaRepository;
    }

    @Override
    public SucursalResponseDTO crear(Long farmaciaId, SucursalCreateDTO dto){
        if(sucursalRepository.existsByFarmacia_FarmaciaIdAndSucursalNombre(farmaciaId, dto.getNombre())){
            throw new DuplicateResourceException("Ya existe una sucursal con ese nombre en tu farmacia: " + dto.getNombre());
        }

        Farmacia farmacia = farmaciaRepository.findById(farmaciaId)
                .orElseThrow(()-> new ResourceNotFoundException("Farmacia no encontrada: " + farmaciaId));

        Sucursal sucursal = Sucursal.builder()
                .sucursalNombre(dto.getNombre())
                .sucursalDireccion(dto.getDireccion())
                .sucursalTelefono(dto.getTelefono())
                .sucursalEstado(true)
                .farmacia(farmacia)
                .build();

        return SucursalResponseDTO.fromEntity(sucursalRepository.save(sucursal));
    }

    @Override
    @Transactional(readOnly = true)
    public SucursalResponseDTO obtenerPorId(Long farmaciaId, Long id){
        return sucursalRepository.findById(id)
                .map(SucursalResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada por ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SucursalSimpleDTO> listarActivasPaginadas(Long farmaciaId, Pageable pageable) {
        return sucursalRepository.findBySucursalEstadoTrue(pageable)
                .map(SucursalSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SucursalSimpleDTO> listarTodasPaginadas(Long farmaciaId, Pageable pageable) {
        return sucursalRepository.findAll(pageable)
                .map(SucursalSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SucursalSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable){
        return sucursalRepository.buscarPorTexto(texto, pageable)
                .map(SucursalSimpleDTO::fromEntity);
    }

    @Override
    public SucursalResponseDTO actualizar(Long farmaciaId, Long id, SucursalUpdateDTO dto){
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada por ID: " + id));

        String nuevoNombre = dto.getNombre().trim();
        if(!sucursal.getSucursalNombre().equalsIgnoreCase(nuevoNombre)){
            if(sucursalRepository.existsByFarmacia_FarmaciaIdAndSucursalNombre(farmaciaId, nuevoNombre)){
                throw new DuplicateResourceException("Ya existe otra sucursal con ese nombre en tu farmacia: " + nuevoNombre);
            }
            sucursal.setSucursalNombre(nuevoNombre);
        }

        sucursal.setSucursalDireccion(dto.getDireccion());
        sucursal.setSucursalTelefono(dto.getTelefono());

        if(dto.getEstado() != null){
            sucursal.setSucursalEstado(dto.getEstado());
        }

        return SucursalResponseDTO.fromEntity(sucursalRepository.save(sucursal));
    }

    @Override
    public void cambiarEstado(Long farmaciaId, Long id, Boolean estado){
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada por ID: " + id));
        sucursal.setSucursalEstado(estado);
        sucursalRepository.save(sucursal);
    }

    @Override
    public void eliminar(Long farmaciaId, Long id){
        cambiarEstado(farmaciaId, id, false);
    }
}