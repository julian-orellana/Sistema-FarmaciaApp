package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.producto.ProductoCreateDTO;
import farmacias.AppOchoa.dto.producto.ProductoResponseDTO;
import farmacias.AppOchoa.dto.producto.ProductoSimpleDTO;
import farmacias.AppOchoa.dto.producto.ProductoUpdateDTO;
import farmacias.AppOchoa.services.ProductoService;
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
@RequestMapping("/api/v1/productos")
@AllArgsConstructor
public class ProductoController extends  BaseController {

    private final ProductoService productoService;
    private final JwtUtil jwtUtil;



    @GetMapping
    public ResponseEntity<Page<ProductoSimpleDTO>> listarProductosActivos(
            @PageableDefault(size = 10, sort = "productoNombre") Pageable pageable) {
        return ResponseEntity.ok(productoService.listarProductosActivos(getFarmaciaId(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(getFarmaciaId(), id));
    }

    @GetMapping("/codigobarras")
    public ResponseEntity<ProductoResponseDTO> obtenerPorCodigoBarras(@RequestParam String codigo) {
        return ResponseEntity.ok(productoService.obtenerPorCodigoBarras(getFarmaciaId(), codigo));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<ProductoSimpleDTO>> buscarPorTexto(
            @RequestParam String texto, Pageable pageable){
        return  ResponseEntity.ok(productoService.buscarPorTexto(getFarmaciaId(), texto, pageable));
    }



    @PostMapping
    public ResponseEntity<ProductoResponseDTO> agregarProducto(@Valid @RequestBody ProductoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.agregarProducto(getFarmaciaId(), dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(
            @PathVariable Long id, @Valid @RequestBody ProductoUpdateDTO dto) {
        return ResponseEntity.ok(productoService.actualizarProducto(getFarmaciaId(), id, dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestParam Boolean estado) {
        productoService.cambiarEstado(getFarmaciaId(), id, estado);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(getFarmaciaId(), id);
        return ResponseEntity.noContent().build();
    }
}