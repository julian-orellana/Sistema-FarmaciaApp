package farmacias.AppOchoa.serviceimpl;



import farmacias.AppOchoa.dto.dashboard.DashboardResponseDTO;
import farmacias.AppOchoa.model.MetodoPagoEstado;
import farmacias.AppOchoa.repository.VentaDetalleRepository;
import farmacias.AppOchoa.repository.VentaPagoRepository;
import farmacias.AppOchoa.repository.VentaRepository;
import farmacias.AppOchoa.services.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
public class DashboardServiceImpl implements DashboardService {
    private final VentaRepository ventaRepository;
    private final VentaPagoRepository ventaPagoRepository;
    private final VentaDetalleRepository ventaDetalleRepository;

    public DashboardServiceImpl(
            VentaRepository ventaRepository,
            VentaPagoRepository ventaPagoRepository,
            VentaDetalleRepository ventaDetalleRepository){
        this.ventaRepository = ventaRepository;
        this.ventaDetalleRepository = ventaDetalleRepository;
        this.ventaPagoRepository = ventaPagoRepository;
    }

    @Override
    public DashboardResponseDTO obtenerResumen(Long farmaciaId, Long sucursalId, LocalDate fechaInicio, LocalDate fechaFin){
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);



        BigDecimal totalVentas = ventaRepository.sumarVentasPorRango(farmaciaId, sucursalId, inicio, fin);
        BigDecimal totalEfectivo = ventaPagoRepository.sumarPorMetodoPago(farmaciaId, sucursalId, MetodoPagoEstado.EFECTIVO, inicio, fin);
        BigDecimal totalTarjeta = ventaPagoRepository.sumarPorMetodoPago(farmaciaId, sucursalId, MetodoPagoEstado.TARJETA_CREDITO, inicio, fin);
        Integer totalUnidades = ventaDetalleRepository.sumarUnidadesVendidas(farmaciaId, sucursalId, inicio, fin);

        return DashboardResponseDTO.builder()
                .totalVentas(totalVentas != null ? totalVentas : BigDecimal.ZERO)
                .totalEfectivo(totalEfectivo != null ? totalEfectivo : BigDecimal.ZERO)
                .totalTarjeta(totalTarjeta != null ? totalTarjeta : BigDecimal.ZERO)
                .totalUnidadesVendidas(totalUnidades != null ? totalUnidades : 0)
                .build();
    }

}
