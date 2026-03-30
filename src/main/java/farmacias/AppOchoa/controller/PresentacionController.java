package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.presentacion.PresentacionCreateDTO;
import farmacias.AppOchoa.dto.presentacion.PresentacionResponseDTO;
import farmacias.AppOchoa.dto.presentacion.PresentacionSimpleDTO;
import farmacias.AppOchoa.dto.presentacion.PresentacionUpdateDTO;
import farmacias.AppOchoa.services.PresentacionService;
import farmacias.AppOchoa.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/presentaciones")
@AllArgsConstructor
public class PresentacionController extends  BaseController{
    private final PresentacionService presentacionService;
    private final JwtUtil jwtUtil;



    @GetMapping
    public ResponseEntity<Page<PresentacionSimpleDTO>> listarActivasPaginadas(
            @PageableDefault(size = 10, sort = "presentacionNombre") Pageable pageable){
        return ResponseEntity.ok(presentacionService.listarActivasPaginadas(getFarmaciaId(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PresentacionResponseDTO> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(presentacionService.obtenerPorId(getFarmaciaId(), id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<PresentacionSimpleDTO>> buscarPorTexto(
            @RequestParam String texto, Pageable pageable){
        return ResponseEntity.ok(presentacionService.buscarPorTexto(getFarmaciaId(), texto, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<PresentacionResponseDTO> crear(@Valid @RequestBody PresentacionCreateDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(presentacionService.crear(getFarmaciaId(), dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PresentacionResponseDTO> actualizar(
            @PathVariable Long id, @Valid @RequestBody PresentacionUpdateDTO dto){
        return ResponseEntity.ok(presentacionService.actualizar(getFarmaciaId(), id, dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestParam Boolean estado){
        presentacionService.cambiarEstado(getFarmaciaId(), id, estado);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        presentacionService.eliminar(getFarmaciaId(), id);
        return ResponseEntity.noContent().build();
    }
}