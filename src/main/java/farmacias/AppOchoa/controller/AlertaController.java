package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.alerta.AlertaCreateDTO;
import farmacias.AppOchoa.dto.alerta.AlertaResponseDTO;
import farmacias.AppOchoa.dto.alerta.AlertaSimpleDTO;
import farmacias.AppOchoa.dto.alerta.AlertaUpdateDTO;
import farmacias.AppOchoa.services.AlertaService;
import farmacias.AppOchoa.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/alertas")
@AllArgsConstructor
public class AlertaController extends BaseController {
    private final AlertaService alertaService;
    private final JwtUtil jwtUtil;


    @GetMapping
    public ResponseEntity<Page<AlertaSimpleDTO>> listarTodasPaginadas(
            @PageableDefault(size = 10, sort = "alertaFecha", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(alertaService.listarTodasPaginadas(getFarmaciaId(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertaResponseDTO> listarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.listarPorId(getFarmaciaId(), id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<AlertaSimpleDTO>> buscarPorTexto(
            @RequestParam String texto, Pageable pageable){
        return  ResponseEntity.ok(alertaService.buscarPorTexto(getFarmaciaId(), texto, pageable));
    }

    @GetMapping("/no-leidas")
    public ResponseEntity<Page<AlertaSimpleDTO>> listarNoLeidasPaginadas(
            @PageableDefault(size = 10, sort = "alertaFecha", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(alertaService.listarNoLeidasPaginadas(getFarmaciaId(), pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<AlertaResponseDTO> crear(@Valid @RequestBody AlertaCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alertaService.crear(getFarmaciaId(), dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AlertaUpdateDTO dto) {
        return ResponseEntity.ok(alertaService.actualizar(getFarmaciaId(), id, dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id) {
        alertaService.cambiarEstado(getFarmaciaId(), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        alertaService.eliminar(getFarmaciaId(), id);
        return ResponseEntity.noContent().build();
    }
}