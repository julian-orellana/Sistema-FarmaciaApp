package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.ventapago.VentaPagoCreateDTO;
import farmacias.AppOchoa.dto.ventapago.VentaPagoResponseDTO;
import farmacias.AppOchoa.dto.ventapago.VentaPagoSimpleDTO;
import farmacias.AppOchoa.exception.BadRequestException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.CajaSesiones;
import farmacias.AppOchoa.model.SesionEstado;
import farmacias.AppOchoa.model.Sucursal;
import farmacias.AppOchoa.model.Venta;
import farmacias.AppOchoa.model.VentaEstado;
import farmacias.AppOchoa.model.VentaPago;
import farmacias.AppOchoa.repository.CajaSesionesRepository;
import farmacias.AppOchoa.repository.SucursalRepository;
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
    private final SucursalRepository sucursalRepository;

    public VentaPagosServiceImpl(
            VentaPagoRepository ventaPagoRepository,
            VentaRepository ventaRepository,
            CajaSesionesRepository cajaSesionesRepository,
            SucursalRepository sucursalRepository){
        this.ventaPagoRepository = ventaPagoRepository;
        this.ventaRepository = ventaRepository;
        this.cajaSesionesRepository = cajaSesionesRepository;
        this.sucursalRepository = sucursalRepository;
    }

    private Sucursal buscarSucursal(Long farmaciaId){
        return sucursalRepository.findByFarmacia_FarmaciaId(farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada para tu farmacia"));
    }

    @Override
    public VentaPagoResponseDTO crear(Long farmaciaId, VentaPagoCreateDTO dto){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        Venta venta = buscarVentas(sucursal.getSucursalId(), dto.getVentaId());
        CajaSesiones cajaSesiones = buscarSesiones(sucursal.getSucursalId(), dto.getCajaSesionId());

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
        // saldo pendiente, no se acepta del cliente
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

        VentaPago ventaPago = VentaPago.builder()
                .venta(venta)
                .cajaSesiones(cajaSesiones)
                .metodoPago(dto.getMetodoPago())
                .referenciaTransaccion(dto.getReferenciaTransaccion())
                .montoRecibido(montoRecibido)
                .montoVuelto(montoVuelto)
                .farmacia(sucursal.getFarmacia())
                .sucursal(sucursal)
                .build();

        return VentaPagoResponseDTO.fromEntity(ventaPagoRepository.save(ventaPago));
    }
    private Venta buscarVentas(Long sucursalId, Long id){
        if(id == null) return null;
        return ventaRepository.findByVentaIdAndSucursal_SucursalId(id, sucursalId)
                .orElseThrow(()-> new ResourceNotFoundException("Venta no encontrada en tu farmacia"));
    }
    private CajaSesiones buscarSesiones(Long sucursalId, Long id){
        if(id == null) return null;
        return cajaSesionesRepository.findBySesionIdAndSucursal_SucursalId(id, sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("Sesion no encontrada en tu farmacia"));
    }

    @Override
    @Transactional(readOnly = true)
    public VentaPagoResponseDTO buscarPorId(Long farmaciaId, Long id){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return ventaPagoRepository.findByPagoIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .map(VentaPagoResponseDTO::fromEntity)
                .orElseThrow(()-> new ResourceNotFoundException("Pago no encontrado por ID"));
    }
    @Override
    @Transactional(readOnly = true)
    public Page<VentaPagoSimpleDTO> listarActivas(Long farmaciaId, Pageable pageable){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return ventaPagoRepository.findBySucursal_SucursalId(sucursal.getSucursalId(), pageable)
                .map(VentaPagoSimpleDTO::fromEntity);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<VentaPagoSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return ventaPagoRepository.buscarPorTexto(sucursal.getSucursalId(), texto, pageable)
                .map(VentaPagoSimpleDTO::fromEntity);
    }
    @Override
    public void eliminar(Long farmaciaId, Long id) {
        throw new UnsupportedOperationException("Por reglas de auditoría financiera, este registro es histórico y no puede ser eliminado ni modificado.");
    }

}
