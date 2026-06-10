package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.ventafel.VentaFelCreateDTO;
import farmacias.AppOchoa.dto.ventafel.VentaFelResponseDTO;
import farmacias.AppOchoa.dto.ventafel.VentaFelSimpleDTO;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.Venta;
import farmacias.AppOchoa.model.VentaFel;
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

    public VentaFelServiceImpl(
            VentaFelRepository ventaFelRepository,
            VentaRepository ventaRepository){
        this.ventaFelRepository = ventaFelRepository;
        this.ventaRepository = ventaRepository;
    }

    @Override
    public VentaFelResponseDTO crear(Long farmaciaId, VentaFelCreateDTO dto){
        Venta venta = buscarVenta(farmaciaId, dto.getVentaId());

        VentaFel ventaFel = VentaFel.builder()
                .venta(venta)
                .build();
        return VentaFelResponseDTO.fromEntity(ventaFelRepository.save(ventaFel));
    }

    private Venta buscarVenta(Long farmaciaId, Long id){
        if(id == null) return null;
        return ventaRepository.findByVentaIdAndSucursal_Farmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(()-> new ResourceNotFoundException("Venta no encontrada en tu farmacia"));
    }

    @Override
    @Transactional(readOnly = true)
    public VentaFelResponseDTO buscarPorId(Long farmaciaId, Long id){
        return ventaFelRepository.findByFelIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .map(VentaFelResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Documento FEL no encontrado por ID"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaFelSimpleDTO> listarActivas(Long farmaciaId, Pageable pageable){
        return ventaFelRepository.findByFarmacia_FarmaciaId(farmaciaId, pageable)
                .map(VentaFelSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaFelSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable) {
        return ventaFelRepository.buscarPorTexto(farmaciaId, texto, pageable)
                .map(VentaFelSimpleDTO::fromEntity);
    }

    @Override
    public void eliminar(Long farmaciaId, Long id) {
        throw new UnsupportedOperationException("Por reglas de auditoría financiera, este registro es histórico y no puede ser eliminado ni modificado.");
    }

}