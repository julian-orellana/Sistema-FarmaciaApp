package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.dashboard.DashboardResponseDTO;
import farmacias.AppOchoa.services.DashboardService;
import farmacias.AppOchoa.util.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/dashboard")
@AllArgsConstructor
@Tag(name = "Dashboard-Controller")
public class DashboardController extends BaseController{
    private final DashboardService service;
    private final JwtUtil jwtUtil;

    @PreAuthorize("hasAuthority('administrador') and principal.farmacia.farmaciaId == #farmaciaId")
    @GetMapping
    public ResponseEntity<DashboardResponseDTO> obtenerResumen(
            @RequestParam Long farmaciaId,
            @RequestParam Long sucursalId,
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin){
        return ResponseEntity.ok(service.obtenerResumen(farmaciaId, sucursalId, fechaInicio, fechaFin));

    }

}
