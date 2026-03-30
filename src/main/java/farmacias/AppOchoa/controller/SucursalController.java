package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.sucursal.SucursalCreateDTO;
import farmacias.AppOchoa.dto.sucursal.SucursalResponseDTO;
import farmacias.AppOchoa.dto.sucursal.SucursalSimpleDTO;
import farmacias.AppOchoa.dto.sucursal.SucursalUpdateDTO;
import farmacias.AppOchoa.services.SucursalService;
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
@RequestMapping("/api/v1/sucursales")
@RequiredArgsConstructor
public class SucursalController extends  BaseController{

    private final SucursalService sucursalService;
    private final JwtUtil jwtUtil;


    @GetMapping
    public ResponseEntity<Page<SucursalSimpleDTO>> listarActivasPaginadas(
            @PageableDefault(size = 10, sort = "sucursalNombre") Pageable pageable) {
        return ResponseEntity.ok(sucursalService.listarActivasPaginadas(getFarmaciaId(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SucursalResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(sucursalService.obtenerPorId(getFarmaciaId(), id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<SucursalSimpleDTO>> buscarPorTexto(
            @RequestParam String texto, Pageable pageable){
        return ResponseEntity.ok(sucursalService.buscarPorTexto(getFarmaciaId(), texto, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<SucursalResponseDTO> crear(@Valid @RequestBody SucursalCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sucursalService.crear(getFarmaciaId(), dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SucursalResponseDTO> actualizar(
            @PathVariable Long id, @Valid @RequestBody SucursalUpdateDTO dto) {
        return ResponseEntity.ok(sucursalService.actualizar(getFarmaciaId(), id, dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestParam Boolean estado) {
        sucursalService.cambiarEstado(getFarmaciaId(), id, estado);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        sucursalService.eliminar(getFarmaciaId(), id);
        return ResponseEntity.noContent().build();
    }
}