package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.inventario.InventarioCreateDTO;
import farmacias.AppOchoa.dto.inventario.InventarioResponseDTO;
import farmacias.AppOchoa.dto.inventario.InventarioSimpleDTO;
import farmacias.AppOchoa.dto.inventario.InventarioUpdateDTO;
import farmacias.AppOchoa.services.InventarioService;
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
@RequestMapping("/api/v1/inventarios")
@AllArgsConstructor
public class InventarioController extends  BaseController{
    private final InventarioService inventarioService;
    private final JwtUtil jwtUtil;


    @GetMapping
    public ResponseEntity<Page<InventarioSimpleDTO>> listarActivosPaginado(
            @PageableDefault(size = 10, sort = "inventarioCantidadActual") Pageable pageable){
        return ResponseEntity.ok(inventarioService.listarActivosPaginado(getFarmaciaId(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioResponseDTO> listarPorId(@PathVariable Long id){
        return ResponseEntity.ok(inventarioService.listaPorId(getFarmaciaId(), id));
    }

    @GetMapping("/todos")
    public ResponseEntity<Page<InventarioSimpleDTO>> listarTodosPaginados(
            @PageableDefault(size = 10, sort = "inventarioCantidadActual") Pageable pageable){
        return ResponseEntity.ok(inventarioService.listarTodosPaginado(getFarmaciaId(), pageable));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<InventarioSimpleDTO>> buscarPorTexto(@RequestParam String texto, Pageable pageable){
        return ResponseEntity.ok(inventarioService.buscarPorTexto(getFarmaciaId(), texto, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<InventarioResponseDTO> crear(@Valid @RequestBody InventarioCreateDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.crear(getFarmaciaId(), dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventarioResponseDTO> actualizar(
            @PathVariable Long id, @Valid @RequestBody InventarioUpdateDTO dto){
        return ResponseEntity.ok(inventarioService.actualizar(getFarmaciaId(), id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        inventarioService.eliminar(getFarmaciaId(), id);
        return ResponseEntity.noContent().build();
    }
}