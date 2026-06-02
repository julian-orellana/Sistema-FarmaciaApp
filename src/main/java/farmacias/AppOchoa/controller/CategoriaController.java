package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.categoria.CategoriaCreateDTO;
import farmacias.AppOchoa.dto.categoria.CategoriaResponseDTO;
import farmacias.AppOchoa.dto.categoria.CategoriaSimpleDTO;
import farmacias.AppOchoa.dto.categoria.CategoriaUpdateDTO;
import farmacias.AppOchoa.services.CategoriaService;
import farmacias.AppOchoa.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaController extends BaseController {

    private final CategoriaService categoriaService;
    private final JwtUtil jwtUtil;



    @GetMapping
    public ResponseEntity<Page<CategoriaSimpleDTO>> listarPaginados(
            @PageableDefault(size = 10, sort = "categoriaNombre") Pageable pageable) {
        return ResponseEntity.ok(categoriaService.listarActivasPaginadas(getFarmaciaId(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(categoriaService.obtenerPorId(getFarmaciaId(), id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<CategoriaSimpleDTO>>buscarPorTexto(
            @RequestParam String texto, Pageable pageable){
        return  ResponseEntity.ok(categoriaService.buscarPorTexto(getFarmaciaId(), texto, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<CategoriaResponseDTO> crear(@Valid @RequestBody CategoriaCreateDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.crear(getFarmaciaId(), dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizar(
            @PathVariable Long id, @Valid @RequestBody CategoriaUpdateDTO dto){
        return ResponseEntity.ok(categoriaService.actualizar(getFarmaciaId(), id, dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestParam Boolean estado){
        categoriaService.cambiarEstado(getFarmaciaId(), id, estado);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        categoriaService.eliminar(getFarmaciaId(), id);
        return ResponseEntity.noContent().build();
    }
}