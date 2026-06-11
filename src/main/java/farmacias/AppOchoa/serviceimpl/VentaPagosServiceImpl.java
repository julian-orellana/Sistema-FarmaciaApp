package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.ventapago.VentaPagoCreateDTO;
import farmacias.AppOchoa.dto.ventapago.VentaPagoResponseDTO;
import farmacias.AppOchoa.dto.ventapago.VentaPagoSimpleDTO;
import farmacias.AppOchoa.exception.BadRequestException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.CajaSesiones;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.SesionEstado;
import farmacias.AppOchoa.model.Venta;
import farmacias.AppOchoa.model.VentaEstado;
import farmacias.AppOchoa.model.VentaPago;
import farmacias.AppOchoa.repository.CajaSesionesRepository;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.repository.VentaPagoRepository;
import farmacias.AppOchoa.repository.VentaRepository;
import farmacias.AppOchoa.services.VentaPagoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class VentaPagosServiceImpl implements VentaPagoService {
    private final VentaPagoRepository ventaPagoRepository;
    private final VentaRepository ventaRepository;
    private final CajaSesionesRepository cajaSesionesRepository;
    private final FarmaciaRepository farmaciaRepository;

    public VentaPagosServiceImpl(
            VentaPagoRepository ventaPagoRepository,
            VentaRepository ventaRepository,
            CajaSesionesRepository cajaSesionesRepository,
            FarmaciaRepository farmaciaRepository){
        this.ventaPagoRepository = ventaPagoRepository;
        this.ventaRepository = ventaRepository;
        this.cajaSesionesRepository = cajaSesionesRepository;
        this.farmaciaRepository = farmaciaRepository;
    }
    @Override
    public VentaPagoResponseDTO crear(Long farmaciaId, VentaPagoCreateDTO dto){
        Venta venta = buscarVentas(farmaciaId, dto.getVentaId());
        CajaSesiones cajaSesiones = buscarSesiones(farmaciaId, dto.getCajaSesionId());

        // No registrar pagos sobre una venta anulada
        if (venta.getVentaEstado() == VentaEstado.anulada) {
            throw new BadRequestException("No se puede registrar un pago sobre una venta anulada");
        }

        // La sesion de caja debe estar abierta para recibir pagos
        if (cajaSesiones.getSesionEstado() != SesionEstado.abierta) {
            throw new BadRequestException("La sesion de caja no esta abierta");
        }

        // El monto recibido debe ser positivo
        BigDecimal montoRecibido = dto.getMontoRecibido();
        if (montoRecibido == null || montoRecibido.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El monto recibido debe ser mayor a 0");
        }

        // Multipago permitido: la suma de lo ya abonado mas este pago no puede
        // exceder el total de la venta. El vuelto lo calcula el servidor sobre el
        // saldo pendiente, no se acepta del cliente (A9).
        BigDecimal total = venta.getVentaTotal();
        BigDecimal yaAbonado = ventaPagoRepository.sumarAbonadoPorVenta(venta.getVentaId());
        BigDecimal saldoPendiente = total.subtract(yaAbonado);

        if (saldoPendiente.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("La venta ya esta totalmente pagada");
        }

        // Vuelto = exceso del monto recibido sobre el saldo pendiente (0 si no excede).
        // El neto aplicado a la venta (recibido - vuelto) nunca supera el saldo.
        BigDecimal montoVuelto = montoRecibido.compareTo(saldoPendiente) > 0
                ? montoRecibido.subtract(saldoPendiente)
                : BigDecimal.ZERO;

        Farmacia farmacia = farmaciaRepository.getReferenceById(farmaciaId);

        VentaPago ventaPago = VentaPago.builder()
                .venta(venta)
                .cajaSesiones(cajaSesiones)
                .metodoPago(dto.getMetodoPago())
                .referenciaTransaccion(dto.getReferenciaTransaccion())
                .montoRecibido(montoRecibido)
                .montoVuelto(montoVuelto)
                .farmacia(farmacia)
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
        return ventaPagoRepository.buscarPorTexto(farmaciaId, texto, pageable)
                .map(VentaPagoSimpleDTO::fromEntity);
    }
    @Override
    public void eliminar(Long farmaciaId, Long id) {
        throw new UnsupportedOperationException("Por reglas de auditoría financiera, este registro es histórico y no puede ser eliminado ni modificado.");
    }

}
