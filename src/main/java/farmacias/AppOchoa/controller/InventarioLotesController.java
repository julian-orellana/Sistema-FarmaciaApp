package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.inventariolotes.InventarioLotesCreateDTO;
import farmacias.AppOchoa.dto.inventariolotes.InventarioLotesResponseDTO;
import farmacias.AppOchoa.dto.inventariolotes.InventarioLotesSimpleDTO;
import farmacias.AppOchoa.dto.inventariolotes.InventarioLotesUpdateDTO;
import farmacias.AppOchoa.services.InventarioLotesService;
import farmacias.AppOchoa.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/inventarioslotes")
@AllArgsConstructor
public class InventarioLotesController extends  BaseController{
    private final InventarioLotesService inventarioLotesService;
    private final JwtUtil jwtUtil;



    @GetMapping
    public ResponseEntity<Page<InventarioLotesSimpleDTO>> listarPorSucursalPaginado(
            @RequestParam Long sucursalId,
            @PageableDefault(size = 10, sort = "loteCantidadActual") Pageable pageable){
        return ResponseEntity.ok(inventarioLotesService.listarPorSucursalPaginado(getFarmaciaId(), sucursalId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioLotesResponseDTO> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(inventarioLotesService.buscarPorId(getFarmaciaId(), id));
    }

    @GetMapping("/proximos-vencer")
    public ResponseEntity<Page<InventarioLotesSimpleDTO>> listarProximosAVencerPaginado(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaLimite,
            @PageableDefault(size = 10, sort = "loteFechaVencimiento") Pageable pageable) {
        return ResponseEntity.ok(inventarioLotesService.listarProximosAVencerPaginado(getFarmaciaId(), fechaLimite, pageable));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<InventarioLotesSimpleDTO>> buscarPorTexto(
            @RequestParam String texto, Pageable pageable){
        return ResponseEntity.ok(inventarioLotesService.buscarPorTexto(getFarmaciaId(), texto, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<InventarioLotesResponseDTO> crear(@Valid @RequestBody InventarioLotesCreateDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioLotesService.crear(getFarmaciaId(), dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventarioLotesResponseDTO> actualizar(
            @PathVariable Long id, @Valid @RequestBody InventarioLotesUpdateDTO dto){
        return ResponseEntity.ok(inventarioLotesService.actualizar(getFarmaciaId(), id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        inventarioLotesService.eliminar(getFarmaciaId(), id);
        return ResponseEntity.noContent().build();
    }
}