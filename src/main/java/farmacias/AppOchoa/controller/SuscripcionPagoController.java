package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.suscripcionpago.SuscripcionPagoCreateDTO;
import farmacias.AppOchoa.dto.suscripcionpago.SuscripcionPagoResponseDTO;
import farmacias.AppOchoa.dto.suscripcionpago.SuscripcionPagoSimpleDTO;
import farmacias.AppOchoa.services.SuscripcionPagoService;
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
@RequestMapping("/api/v1/suscripciones")
@AllArgsConstructor
public class SuscripcionPagoController extends  BaseController{
    private final SuscripcionPagoService suscripcionPagoService;
    private final JwtUtil jwtUtil;



    // Los pagos de suscripcion son registros financieros de la farmacia:
    // su consulta y registro se restringen a roles administrativos (M3).
    @GetMapping
    @PreAuthorize("hasAnyAuthority('administrador','superadmin')")
    public ResponseEntity<Page<SuscripcionPagoSimpleDTO>> listarSuscripciones(
            @PageableDefault(size = 10, sort = "pagoPlan") Pageable pageable){
        return ResponseEntity.ok(suscripcionPagoService.listarSuscripciones(getFarmaciaId(), pageable));
    }

    @GetMapping("/buscar/{id}")
    @PreAuthorize("hasAnyAuthority('administrador','superadmin')")
    public ResponseEntity<SuscripcionPagoResponseDTO> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(suscripcionPagoService.buscarPorId(getFarmaciaId(), id));
    }

    @GetMapping("/buscar/texto")
    @PreAuthorize("hasAnyAuthority('administrador','superadmin')")
    public ResponseEntity<Page<SuscripcionPagoSimpleDTO>> buscarPorTexto(
            @RequestParam String texto,
            @PageableDefault(size = 10, sort = "pagoPlan") Pageable pageable){
        return ResponseEntity.ok(suscripcionPagoService.buscarPorTexto(getFarmaciaId(), texto, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('administrador','superadmin')")
    public ResponseEntity<SuscripcionPagoResponseDTO> crear(@Valid @RequestBody SuscripcionPagoCreateDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(suscripcionPagoService.crear(getFarmaciaId(), dto));
    }

    @DeleteMapping("/buscar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        suscripcionPagoService.eliminar(getFarmaciaId(), id);
        return ResponseEntity.noContent().build();
    }
}