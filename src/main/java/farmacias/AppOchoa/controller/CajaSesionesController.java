package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.cajasesiones.CajaSesionesCreateDTO;
import farmacias.AppOchoa.dto.cajasesiones.CajaSesionesResponseDTO;
import farmacias.AppOchoa.dto.cajasesiones.CajaSesionesSimpleDTO;
import farmacias.AppOchoa.services.CajaSesionesService;
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
@RequestMapping("/api/v1/cajasesiones")
@AllArgsConstructor
@Tag(name = "CajaSesiones-controller")
public class CajaSesionesController extends BaseController{
    private final CajaSesionesService cajaSesionesService;
    private final JwtUtil jwtUtil;



    @GetMapping
    public ResponseEntity<Page<CajaSesionesSimpleDTO>> listarSesiones(
            @PageableDefault(size = 10, sort = "sesionFechaApertura") Pageable pageable){
        return ResponseEntity.ok(cajaSesionesService.listarSesiones(getFarmaciaId(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CajaSesionesResponseDTO> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(cajaSesionesService.buscarPorId(getFarmaciaId(), id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<CajaSesionesSimpleDTO>> buscar(
            @RequestParam String texto, Pageable pageable) {
        return ResponseEntity.ok(cajaSesionesService.buscarPorTexto(getFarmaciaId(), texto, pageable));
    }

    @PostMapping
    public ResponseEntity<CajaSesionesResponseDTO> crear(@Valid @RequestBody CajaSesionesCreateDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(cajaSesionesService.crear(getFarmaciaId(), dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        cajaSesionesService.eliminar(getFarmaciaId(), id);
        return ResponseEntity.noContent().build();
    }
}