package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.caja.CajaCreateDTO;
import farmacias.AppOchoa.dto.caja.CajaResponseDTO;
import farmacias.AppOchoa.dto.caja.CajaSimpleDTO;
import farmacias.AppOchoa.dto.caja.CajaUpdateDTO;
import farmacias.AppOchoa.model.CajaEstado;
import farmacias.AppOchoa.services.CajaService;
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
@RequestMapping("/api/v1/caja")
@AllArgsConstructor
@Tag(name = "Caja-controller")
public class CajaController extends  BaseController{
    private final CajaService cajaService;
    private final JwtUtil jwtUtil;



    @GetMapping
    public ResponseEntity<Page<CajaSimpleDTO>> listarCajasActivas(
            @PageableDefault(size = 10, sort = "cajaNombre") Pageable pageable){
        return ResponseEntity.ok(cajaService.listarCajasActivas(getFarmaciaId(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CajaResponseDTO> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(cajaService.buscarPorId(getFarmaciaId(), id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<CajaSimpleDTO>> buscar(
            @RequestParam String texto, Pageable pageable) {
        return ResponseEntity.ok(cajaService.buscarPorTexto(getFarmaciaId(), texto, pageable));
    }

    @PostMapping
    public ResponseEntity<CajaResponseDTO> crearCaja(@Valid @RequestBody CajaCreateDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(cajaService.crearCaja(getFarmaciaId(), dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CajaResponseDTO> actualizarCaja(
            @PathVariable Long id, @Valid @RequestBody CajaUpdateDTO dto){
        return ResponseEntity.ok(cajaService.actualizarCaja(getFarmaciaId(), id, dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id, @RequestBody CajaEstado cajaEstado){
        cajaService.cambiarEstado(getFarmaciaId(), id, cajaEstado);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        cajaService.eliminar(getFarmaciaId(), id);
        return ResponseEntity.noContent().build();
    }
}