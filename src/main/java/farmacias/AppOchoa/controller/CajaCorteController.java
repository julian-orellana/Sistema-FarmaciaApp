package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.cajacorte.CajaCorteCreateDTO;
import farmacias.AppOchoa.dto.cajacorte.CajaCorteResponseDTO;
import farmacias.AppOchoa.dto.cajacorte.CajaCorteSimpleDTO;
import farmacias.AppOchoa.services.CajaCorteService;
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
@RequestMapping("/api/v1/cajacorte")
@AllArgsConstructor
@Tag(name = "Caja Corte-Controller")
public class CajaCorteController extends BaseController {
    private final CajaCorteService cajaCorteService;
    private final JwtUtil jwtUtil;


    @GetMapping
    public ResponseEntity<Page<CajaCorteSimpleDTO>> listarCortes(
            @PageableDefault(size = 10, sort = "corteTotalEfectivo") Pageable pageable){
        return ResponseEntity.ok(cajaCorteService.listarCortes(getFarmaciaId(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CajaCorteResponseDTO> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(cajaCorteService.buscarPorId(getFarmaciaId(), id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<CajaCorteSimpleDTO>> buscar(
            @RequestParam String texto, Pageable pageable) {
        return ResponseEntity.ok(cajaCorteService.buscarPorTexto(getFarmaciaId(), texto, pageable));
    }

    @PostMapping
    public ResponseEntity<CajaCorteResponseDTO> crear(@Valid @RequestBody CajaCorteCreateDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(cajaCorteService.crear(getFarmaciaId(), dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        cajaCorteService.eliminar(getFarmaciaId(), id);
        return ResponseEntity.noContent().build();
    }
}