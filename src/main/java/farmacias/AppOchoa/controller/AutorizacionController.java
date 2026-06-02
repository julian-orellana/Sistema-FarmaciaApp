package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.autorizacion.AutorizacionCreateDTO;
import farmacias.AppOchoa.dto.autorizacion.AutorizacionResponseDTO;
import farmacias.AppOchoa.dto.autorizacion.AutorizacionSimpleDTO;
import farmacias.AppOchoa.services.AutorizacionService;
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
@RequestMapping("/api/v1/autorizaciones")
@AllArgsConstructor
@Tag(name = "Autorizaciones-controller")
public class AutorizacionController {
    private final AutorizacionService autorizacionService;
    private final JwtUtil jwtUtil;

    private Long getFarmaciaId(){
        String token = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getCredentials();
        return jwtUtil.extractFarmaciaId(token);
    }

    @GetMapping
    public ResponseEntity<Page<AutorizacionSimpleDTO>> listarTodas(
            @PageableDefault(size = 10, sort = "autorizacionTipo") Pageable pageable){
        return ResponseEntity.ok(autorizacionService.listarTodas(getFarmaciaId(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutorizacionResponseDTO> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(autorizacionService.buscarPorId(getFarmaciaId(), id));
    }

    @PostMapping
    public ResponseEntity<AutorizacionResponseDTO> crear(@Valid @RequestBody AutorizacionCreateDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(autorizacionService.crear(getFarmaciaId(), dto));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<AutorizacionSimpleDTO>> buscar(
            @RequestParam String texto, Pageable pageable) {
        return ResponseEntity.ok(autorizacionService.buscarPorTexto(getFarmaciaId(), texto, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        autorizacionService.eliminar(getFarmaciaId(), id);
        return ResponseEntity.noContent().build();
    }
}