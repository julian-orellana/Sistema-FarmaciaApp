package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.venta.VentaCreateDTO;
import farmacias.AppOchoa.dto.venta.VentaResponseDTO;
import farmacias.AppOchoa.dto.venta.VentaSimpleDTO;
import farmacias.AppOchoa.dto.venta.VentaUpdateDTO;
import farmacias.AppOchoa.model.VentaEstado;
import farmacias.AppOchoa.services.VentaService;
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
@RequestMapping("/api/v1/ventas")
@AllArgsConstructor
public class VentaController extends  BaseController{
    private final VentaService ventaService;
    private final JwtUtil jwtUtil;



    @GetMapping
    public ResponseEntity<Page<VentaSimpleDTO>> listarActivasPaginadas(
            @PageableDefault(size = 10, sort = "ventaFecha") Pageable pageable){
        return ResponseEntity.ok(ventaService.listarActivasPaginadas(getFarmaciaId(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> listarPorId(@PathVariable Long id){
        return ResponseEntity.ok(ventaService.listarPorId(getFarmaciaId(), id));
    }

    @GetMapping("/todas")
    public ResponseEntity<Page<VentaSimpleDTO>> listarTodasPaginadas(
            @PageableDefault(size = 10, sort = "ventaFecha") Pageable pageable){
        return ResponseEntity.ok(ventaService.listarActivasPaginadas(getFarmaciaId(), pageable));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<VentaSimpleDTO>> buscarPorTexto(
            @RequestParam String texto, Pageable pageable){
        return  ResponseEntity.ok(ventaService.buscarPorTexto(getFarmaciaId(), texto, pageable));
    }

    @PostMapping
    public ResponseEntity<VentaResponseDTO> crear(@Valid @RequestBody VentaCreateDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.crear(getFarmaciaId(), dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> actualizar(
            @PathVariable Long id, @Valid @RequestBody VentaUpdateDTO dto){
        return ResponseEntity.ok(ventaService.actualizar(getFarmaciaId(), id, dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id, @RequestBody VentaEstado ventaEstado){
        ventaService.cambiarEstado(getFarmaciaId(), id, ventaEstado);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        ventaService.eliminar(getFarmaciaId(), id);
        return ResponseEntity.noContent().build();
    }
}