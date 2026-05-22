package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.caja.CajaCreateDTO;
import farmacias.AppOchoa.dto.caja.CajaResponseDTO;
import farmacias.AppOchoa.dto.caja.CajaSimpleDTO;
import farmacias.AppOchoa.dto.caja.CajaUpdateDTO;
import farmacias.AppOchoa.exception.DuplicateResourceException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.Caja;
import farmacias.AppOchoa.model.CajaEstado;
import farmacias.AppOchoa.model.Sucursal;
import farmacias.AppOchoa.repository.CajaRepository;
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

    public CajaServiceImpl(
            CajaRepository cajaRepository,
            SucursalRepository sucursalRepository){
        this.cajaRepository = cajaRepository;
        this.sucursalRepository = sucursalRepository;
    }

    @Override
    public CajaResponseDTO crearCaja(Long farmaciaId, CajaCreateDTO dto){
        if(cajaRepository.existsBySucursalSucursalIdAndCajaNombre(dto.getSucursalId(), dto.getCajaNombre())){
            throw new DuplicateResourceException("Ya existe una caja con ese nombre");
        }
        Sucursal sucursal =  buscarSucursal(dto.getSucursalId());

        Caja caja = Caja.builder()
                .cajaNombre(dto.getCajaNombre())
                .sucursal(sucursal)
                .cajaEstado(CajaEstado.activa)
                .build();

        return CajaResponseDTO.fromEntity(cajaRepository.save(caja));
    }
    private Sucursal buscarSucursal(Long id){
        if(id == null) return  null;
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada por ID"));
    }

    @Override
    @Transactional(readOnly = true)
    public CajaResponseDTO buscarPorId(Long farmaciaId, Long id){
        return cajaRepository.findById(id)
                .map(CajaResponseDTO:: fromEntity)
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
        return cajaRepository.buscarPorTexto(texto, pageable)
                .map(CajaSimpleDTO::fromEntity);
    }

    @Override
    public CajaResponseDTO actualizarCaja(Long farmaciaId, Long id, CajaUpdateDTO dto){
        Caja caja = cajaRepository.findById(id)
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
        Caja caja = cajaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Caja no encontrada por ID"));
        caja.setCajaEstado(cajaEstado);
        cajaRepository.save(caja);
    }
    @Override
    public void eliminar(Long farmaciaId, Long id){
        cambiarEstado(farmaciaId, id, CajaEstado.desactivada);
    }

}