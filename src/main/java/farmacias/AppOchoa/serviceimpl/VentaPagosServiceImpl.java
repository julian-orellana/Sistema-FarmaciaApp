package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.ventapago.VentaPagoCreateDTO;
import farmacias.AppOchoa.dto.ventapago.VentaPagoResponseDTO;
import farmacias.AppOchoa.dto.ventapago.VentaPagoSimpleDTO;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.CajaSesiones;
import farmacias.AppOchoa.model.Venta;
import farmacias.AppOchoa.model.VentaPago;
import farmacias.AppOchoa.repository.CajaSesionesRepository;
import farmacias.AppOchoa.repository.VentaPagoRepository;
import farmacias.AppOchoa.repository.VentaRepository;
import farmacias.AppOchoa.services.VentaPagoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VentaPagosServiceImpl implements VentaPagoService {
    private final VentaPagoRepository ventaPagoRepository;
    private final VentaRepository ventaRepository;
    private final CajaSesionesRepository cajaSesionesRepository;

    public VentaPagosServiceImpl(
            VentaPagoRepository ventaPagoRepository,
            VentaRepository ventaRepository,
            CajaSesionesRepository cajaSesionesRepository){
        this.ventaPagoRepository = ventaPagoRepository;
        this.ventaRepository = ventaRepository;
        this.cajaSesionesRepository = cajaSesionesRepository;
    }
    @Override
    public VentaPagoResponseDTO crear(Long farmaciaId, VentaPagoCreateDTO dto){
        Venta venta = buscarVentas(farmaciaId, dto.getVentaId());
        CajaSesiones cajaSesiones = buscarSesiones(farmaciaId, dto.getCajaSesionId());

        VentaPago ventaPago = VentaPago.builder()
                .venta(venta)
                .cajaSesiones(cajaSesiones)
                .metodoPago(dto.getMetodoPago())
                .referenciaTransaccion(dto.getReferenciaTransaccion())
                .montoRecibido(dto.getMontoRecibido())
                .montoVuelto(dto.getMontoVuelto())
                .build();

        return VentaPagoResponseDTO.fromEntity(ventaPagoRepository.save(ventaPago));
    }
    private Venta buscarVentas(Long farmaciaId, Long id){
        if(id == null) return null;
        return ventaRepository.findByVentaIdAndSucursal_Farmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(()-> new ResourceNotFoundException("Venta no encontrada en tu farmacia"));
    }
    private CajaSesiones buscarSesiones(Long farmaciaId, Long id){
        if(id == null) return null;
        return cajaSesionesRepository.findBySesionIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sesion no encontrada en tu farmacia"));
    }

    @Override
    @Transactional(readOnly = true)
    public VentaPagoResponseDTO buscarPorId(Long farmaciaId, Long id){
        return ventaPagoRepository.findByPagoIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .map(VentaPagoResponseDTO::fromEntity)
                .orElseThrow(()-> new ResourceNotFoundException("Pago no encontrado por ID"));
    }
    @Override
    @Transactional(readOnly = true)
    public Page<VentaPagoSimpleDTO> listarActivas(Long farmaciaId, Pageable pageable){
        return ventaPagoRepository.findByFarmacia_FarmaciaId(farmaciaId, pageable)
                .map(VentaPagoSimpleDTO::fromEntity);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<VentaPagoSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable) {
        return ventaPagoRepository.buscarPorTexto(texto, pageable)
                .map(VentaPagoSimpleDTO::fromEntity);
    }
    @Override
    public void eliminar(Long farmaciaId, Long id) {
        throw new UnsupportedOperationException("Por reglas de auditoría financiera, este registro es histórico y no puede ser eliminado ni modificado.");
    }

}
