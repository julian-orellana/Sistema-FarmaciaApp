package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.ventafel.VentaFelCreateDTO;
import farmacias.AppOchoa.dto.ventafel.VentaFelResponseDTO;
import farmacias.AppOchoa.dto.ventafel.VentaFelSimpleDTO;
import farmacias.AppOchoa.services.VentaFelService;
import farmacias.AppOchoa.util.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ventasfel")
@AllArgsConstructor
@Tag(name = "Ventas Fel-controller")
public class VentaFelController extends  BaseController{

    private final VentaFelService ventaFelService;
    private final JwtUtil jwtUtil;



    @GetMapping
    public ResponseEntity<Page<VentaFelSimpleDTO>> listarActivas(
            @PageableDefault(size = 10, sort = "felNumeroAutorizacion") Pageable pageable) {
        return ResponseEntity.ok(ventaFelService.listarActivas(getFarmaciaId(), pageable));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<VentaFelSimpleDTO>> buscar(
            @RequestParam String texto, Pageable pageable) {
        return ResponseEntity.ok(ventaFelService.buscarPorTexto(getFarmaciaId(), texto, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaFelResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventaFelService.buscarPorId(getFarmaciaId(), id));
    }

    @PostMapping
    public ResponseEntity<VentaFelResponseDTO> crear(@Valid @RequestBody VentaFelCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaFelService.crear(getFarmaciaId(), dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ventaFelService.eliminar(getFarmaciaId(), id);
        return ResponseEntity.noContent().build();
    }
}