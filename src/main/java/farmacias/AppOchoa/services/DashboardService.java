package farmacias.AppOchoa.services;

import farmacias.AppOchoa.dto.dashboard.DashboardResponseDTO;

import java.time.LocalDate;

public interface DashboardService{
     DashboardResponseDTO obtenerResumen(Long farmaciaId, Long sucursalId, LocalDate fechaInicio, LocalDate fechaFin);
}
