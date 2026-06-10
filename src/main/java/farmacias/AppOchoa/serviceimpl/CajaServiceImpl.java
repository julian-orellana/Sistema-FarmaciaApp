package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.caja.CajaCreateDTO;
import farmacias.AppOchoa.dto.caja.CajaResponseDTO;
import farmacias.AppOchoa.dto.caja.CajaSimpleDTO;
import farmacias.AppOchoa.dto.caja.CajaUpdateDTO;
import farmacias.AppOchoa.exception.DuplicateResourceException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.Caja;
import farmacias.AppOchoa.model.CajaEstado;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.Sucursal;
import farmacias.AppOchoa.repository.CajaRepository;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.repository.SucursalRepository;
import farmacias.AppOchoa.services.CajaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CajaServiceImpl implements CajaService {
    private final CajaRepository cajaRepository;
    private final SucursalRepository sucursalRepository;
    private final FarmaciaRepository farmaciaRepository;

    public CajaServiceImpl(
            CajaRepository cajaRepository,
            SucursalRepository sucursalRepository,
            FarmaciaRepository farmaciaRepository){
        this.cajaRepository = cajaRepository;
        this.sucursalRepository = sucursalRepository;
        this.farmaciaRepository = farmaciaRepository;
    }

    @Override
    public CajaResponseDTO crearCaja(Long farmaciaId, CajaCreateDTO dto){
        if(cajaRepository.existsBySucursalSucursalIdAndCajaNombre(dto.getSucursalId(), dto.getCajaNombre())){
            throw new DuplicateResourceException("Ya existe una caja con ese nombre");
        }
        Sucursal sucursal =  buscarSucursal(farmaciaId, dto.getSucursalId());
        Farmacia farmacia = farmaciaRepository.getReferenceById(farmaciaId);

        Caja caja = Caja.builder()
                .cajaNombre(dto.getCajaNombre())
                .sucursal(sucursal)
                .cajaEstado(CajaEstado.activa)
                .farmacia(farmacia)
                .build();

        return CajaResponseDTO.fromEntity(cajaRepository.save(caja));
    }
    private Sucursal buscarSucursal(Long farmaciaId, Long id){
        if(id == null) return  null;
        return sucursalRepository.findBySucursalIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada en tu farmacia"));
    }

    @Override
    @Transactional(readOnly = true)
    public CajaResponseDTO buscarPorId(Long farmaciaId, Long id){
        return cajaRepository.findByCajaIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .map(CajaResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Caja no encontrada por ID"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CajaSimpleDTO> listarCajasActivas(Long farmaciaId, Pageable pageable){
        return cajaRepository.findByCajaEstado(CajaEstado.activa, pageable)
                .map(CajaSimpleDTO::fromEntity);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<CajaSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable) {
        return cajaRepository.buscarPorTexto(farmaciaId, texto, pageable)
                .map(CajaSimpleDTO::fromEntity);
    }

    @Override
    public CajaResponseDTO actualizarCaja(Long farmaciaId, Long id, CajaUpdateDTO dto){
        Caja caja = cajaRepository.findByCajaIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Caja no encontrada por ID"));
        //Validar unicidad de nombre
        if(!caja.getCajaNombre().equalsIgnoreCase(dto.getCajaNombre()) &&
                cajaRepository.existsBySucursalSucursalIdAndCajaNombre(caja.getSucursal().getSucursalId(), dto.getCajaNombre())){
            throw new DuplicateResourceException("Ya existe otra caja con ese nombre: " + dto.getCajaNombre());
        }
        //Actualizar relaciones
        caja.setCajaNombre(dto.getCajaNombre());

        return CajaResponseDTO.fromEntity(cajaRepository.save(caja));
    }
    @Override
    public void cambiarEstado(Long farmaciaId, Long id, CajaEstado cajaEstado){
        Caja caja = cajaRepository.findByCajaIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Caja no encontrada por ID"));
        caja.setCajaEstado(cajaEstado);
        cajaRepository.save(caja);
    }
    @Override
    public void eliminar(Long farmaciaId, Long id){
        cambiarEstado(farmaciaId, id, CajaEstado.desactivada);
    }

}