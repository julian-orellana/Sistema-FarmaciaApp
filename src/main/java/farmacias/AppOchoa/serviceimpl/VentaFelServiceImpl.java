package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.ventafel.VentaFelCreateDTO;
import farmacias.AppOchoa.dto.ventafel.VentaFelResponseDTO;
import farmacias.AppOchoa.dto.ventafel.VentaFelSimpleDTO;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.Sucursal;
import farmacias.AppOchoa.model.Venta;
import farmacias.AppOchoa.model.VentaFel;
import farmacias.AppOchoa.repository.SucursalRepository;
import farmacias.AppOchoa.repository.VentaFelRepository;
import farmacias.AppOchoa.repository.VentaRepository;
import farmacias.AppOchoa.services.VentaFelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VentaFelServiceImpl implements VentaFelService {
    private final VentaFelRepository ventaFelRepository;
    private final VentaRepository ventaRepository;
    private final SucursalRepository sucursalRepository;

    public VentaFelServiceImpl(
            VentaFelRepository ventaFelRepository,
            VentaRepository ventaRepository,
            SucursalRepository sucursalRepository){
        this.ventaFelRepository = ventaFelRepository;
        this.ventaRepository = ventaRepository;
        this.sucursalRepository = sucursalRepository;
    }

    private Sucursal buscarSucursal(Long farmaciaId){
        return sucursalRepository.findByFarmacia_FarmaciaId(farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada para tu farmacia"));
    }

    @Override
    public VentaFelResponseDTO crear(Long farmaciaId, VentaFelCreateDTO dto){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        Venta venta = buscarVenta(sucursal.getSucursalId(), dto.getVentaId());

        VentaFel ventaFel = VentaFel.builder()
                .venta(venta)
                .farmacia(sucursal.getFarmacia())
                .sucursal(sucursal)
                .build();
        return VentaFelResponseDTO.fromEntity(ventaFelRepository.save(ventaFel));
    }

    private Venta buscarVenta(Long sucursalId, Long id){
        if(id == null) return null;
        return ventaRepository.findByVentaIdAndSucursal_SucursalId(id, sucursalId)
                .orElseThrow(()-> new ResourceNotFoundException("Venta no encontrada en tu farmacia"));
    }

    @Override
    @Transactional(readOnly = true)
    public VentaFelResponseDTO buscarPorId(Long farmaciaId, Long id){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return ventaFelRepository.findByFelIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .map(VentaFelResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Documento FEL no encontrado por ID"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaFelSimpleDTO> listarActivas(Long farmaciaId, Pageable pageable){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return ventaFelRepository.findBySucursal_SucursalId(sucursal.getSucursalId(), pageable)
                .map(VentaFelSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaFelSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return ventaFelRepository.buscarPorTexto(sucursal.getSucursalId(), texto, pageable)
                .map(VentaFelSimpleDTO::fromEntity);
    }

    @Override
    public void eliminar(Long farmaciaId, Long id) {
        throw new UnsupportedOperationException("Por reglas de auditoría financiera, este registro es histórico y no puede ser eliminado ni modificado.");
    }

}