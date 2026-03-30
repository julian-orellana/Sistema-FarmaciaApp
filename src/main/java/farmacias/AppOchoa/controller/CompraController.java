package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.compra.CompraCreateDTO;
import farmacias.AppOchoa.dto.compra.CompraResponseDTO;
import farmacias.AppOchoa.dto.compra.CompraSimpleDTO;
import farmacias.AppOchoa.dto.compra.CompraUpdateDTO;
import farmacias.AppOchoa.model.CompraEstado;
import farmacias.AppOchoa.services.CompraService;
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
@RequestMapping("/api/v1/compras")
@AllArgsConstructor
public class CompraController extends  BaseController{
    private final CompraService compraService;
    private final JwtUtil jwtUtil;



    @GetMapping
    public ResponseEntity<Page<CompraSimpleDTO>> listarActivasPaginadas(
            @PageableDefault(size = 10, sort = "compraFecha") Pageable pageable){
        return ResponseEntity.ok(compraService.listarActivasPaginadas(getFarmaciaId(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraResponseDTO> listarPorId(@PathVariable Long id){
        return ResponseEntity.ok(compraService.listarPorId(getFarmaciaId(), id));
    }

    @GetMapping("/todas")
    public ResponseEntity<Page<CompraSimpleDTO>> listarTodasPaginadas(
            @PageableDefault(size = 10, sort = "compraFecha") Pageable pageable){
        return ResponseEntity.ok(compraService.listarActivasPaginadas(getFarmaciaId(), pageable));
    }
    @GetMapping("/buscar")
    public ResponseEntity<Page<CompraSimpleDTO>> buscarPorTexto(@RequestParam String texto, Pageable pageable){
        return ResponseEntity.ok(compraService.buscarPorTexto(getFarmaciaId(), texto, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<CompraResponseDTO> crear(@Valid @RequestBody CompraCreateDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(compraService.crear(getFarmaciaId(), dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompraResponseDTO> actualizar(
            @PathVariable Long id, @Valid @RequestBody CompraUpdateDTO dto){
        return ResponseEntity.ok(compraService.actualizar(getFarmaciaId(), id, dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id, @RequestParam CompraEstado compraEstado){
        compraService.cambiarEstado(getFarmaciaId(), id, compraEstado);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        compraService.eliminar(getFarmaciaId(), id);
        return ResponseEntity.noContent().build();
    }
}