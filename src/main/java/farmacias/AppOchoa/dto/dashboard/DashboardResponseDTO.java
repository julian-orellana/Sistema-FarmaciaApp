package farmacias.AppOchoa.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardResponseDTO {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal totalVentas;
    private BigDecimal totalEfectivo;
    private BigDecimal totalTarjeta;
    private Integer totalUnidadesVendidas;


}
