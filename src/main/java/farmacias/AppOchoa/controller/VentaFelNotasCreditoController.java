package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.ventafelnotascredito.VentaFelNotasCreditoCreateDTO;
import farmacias.AppOchoa.dto.ventafelnotascredito.VentaFelNotasCreditoResponseDTO;
import farmacias.AppOchoa.dto.ventafelnotascredito.VentaFelNotasCreditoSimpleDTO;
import farmacias.AppOchoa.services.VentaFelNotasCreditoService;
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
@RequestMapping("/api/v1/ventasnotascreditos")
@AllArgsConstructor
@Tag(name = "Ventas Fel Notas Credito-controller")
public class VentaFelNotasCreditoController extends  BaseController {
    private final VentaFelNotasCreditoService ventaFelNotasCreditoService;
    private final JwtUtil jwtUtil;


    @GetMapping("/buscar")
    public ResponseEntity<Page<VentaFelNotasCreditoSimpleDTO>> buscar(
            @RequestParam String texto, Pageable pageable) {
        return ResponseEntity.ok(ventaFelNotasCreditoService.buscarPorTexto(getFarmaciaId(), texto, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaFelNotasCreditoResponseDTO> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(ventaFelNotasCreditoService.buscarPorId(getFarmaciaId(), id));
    }

    @GetMapping
    public ResponseEntity<Page<VentaFelNotasCreditoSimpleDTO>> listarNotas(
            @PageableDefault(size = 10, sort = "notaEstado") Pageable pageable){
        return ResponseEntity.ok(ventaFelNotasCreditoService.listarNotas(getFarmaciaId(), pageable));
    }

    @PostMapping
    public ResponseEntity<VentaFelNotasCreditoResponseDTO> crear(@Valid @RequestBody VentaFelNotasCreditoCreateDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaFelNotasCreditoService.crear(getFarmaciaId(), dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        ventaFelNotasCreditoService.eliminar(getFarmaciaId(), id);
        return ResponseEntity.noContent().build();
    }
}