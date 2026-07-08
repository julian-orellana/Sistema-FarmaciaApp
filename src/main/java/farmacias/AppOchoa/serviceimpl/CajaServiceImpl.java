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
        Sucursal sucursal = buscarSucursal(farmaciaId);

        if(cajaRepository.existsBySucursalSucursalIdAndCajaNombre(sucursal.getSucursalId(), dto.getCajaNombre())){
            throw new DuplicateResourceException("Ya existe una caja con ese nombre");
        }

        Caja caja = Caja.builder()
                .cajaNombre(dto.getCajaNombre())
                .sucursal(sucursal)
                .cajaEstado(CajaEstado.activa)
                .build();

        return CajaResponseDTO.fromEntity(cajaRepository.save(caja));
    }

    private Sucursal buscarSucursal(Long farmaciaId){
        return sucursalRepository.findByFarmacia_FarmaciaId(farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada para tu farmacia"));
    }

    @Override
    @Transactional(readOnly = true)
    public CajaResponseDTO buscarPorId(Long farmaciaId, Long id){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return cajaRepository.findByCajaIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .map(CajaResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Caja no encontrada por ID"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CajaSimpleDTO> listarCajasActivas(Long farmaciaId, Pageable pageable){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return cajaRepository.findBySucursal_SucursalIdAndCajaEstado(sucursal.getSucursalId(), CajaEstado.activa, pageable)
                .map(CajaSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CajaSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return cajaRepository.buscarPorTexto(sucursal.getSucursalId(), texto, pageable)
                .map(CajaSimpleDTO::fromEntity);
    }

    @Override
    public CajaResponseDTO actualizarCaja(Long farmaciaId, Long id, CajaUpdateDTO dto){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        Caja caja = cajaRepository.findByCajaIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Caja no encontrada por ID"));

        if(!caja.getCajaNombre().equalsIgnoreCase(dto.getCajaNombre()) &&
                cajaRepository.existsBySucursalSucursalIdAndCajaNombre(sucursal.getSucursalId(), dto.getCajaNombre())){
            throw new DuplicateResourceException("Ya existe otra caja con ese nombre: " + dto.getCajaNombre());
        }

        caja.setCajaNombre(dto.getCajaNombre());

        return CajaResponseDTO.fromEntity(cajaRepository.save(caja));
    }

    @Override
    public void cambiarEstado(Long farmaciaId, Long id, CajaEstado cajaEstado){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        Caja caja = cajaRepository.findByCajaIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Caja no encontrada por ID"));
        caja.setCajaEstado(cajaEstado);
        cajaRepository.save(caja);
    }

    @Override
    public void eliminar(Long farmaciaId, Long id){
        cambiarEstado(farmaciaId, id, CajaEstado.desactivada);
    }
}