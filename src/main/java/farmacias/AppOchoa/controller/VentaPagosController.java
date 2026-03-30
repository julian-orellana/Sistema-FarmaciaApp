package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.ventapago.VentaPagoCreateDTO;
import farmacias.AppOchoa.dto.ventapago.VentaPagoResponseDTO;
import farmacias.AppOchoa.dto.ventapago.VentaPagoSimpleDTO;
import farmacias.AppOchoa.services.VentaPagoService;
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
@RequestMapping("/api/v1/ventapagos")
@AllArgsConstructor
@Tag(name = "Ventas pagos-controller")
public class VentaPagosController extends  BaseController{
    private final VentaPagoService ventaPagoService;
    private final JwtUtil jwtUtil;



    @GetMapping
    public ResponseEntity<Page<VentaPagoSimpleDTO>> listarActivas(
            @PageableDefault(size = 10, sort = "auditoriaFechaCreacion") Pageable pageable){
        return ResponseEntity.ok(ventaPagoService.listarActivas(getFarmaciaId(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaPagoResponseDTO> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(ventaPagoService.buscarPorId(getFarmaciaId(), id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<VentaPagoSimpleDTO>> buscar(
            @RequestParam String texto, Pageable pageable) {
        return ResponseEntity.ok(ventaPagoService.buscarPorTexto(getFarmaciaId(), texto, pageable));
    }

    @PostMapping
    public ResponseEntity<VentaPagoResponseDTO> crear(@Valid @RequestBody VentaPagoCreateDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaPagoService.crear(getFarmaciaId(), dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        ventaPagoService.eliminar(getFarmaciaId(), id);
        return ResponseEntity.noContent().build();
    }
}