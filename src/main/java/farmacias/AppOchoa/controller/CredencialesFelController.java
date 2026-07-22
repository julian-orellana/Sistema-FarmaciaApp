package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.credencialesfel.CredencialesCreateDTO;
import farmacias.AppOchoa.dto.credencialesfel.CredencialesResponseDTO;
import farmacias.AppOchoa.dto.credencialesfel.CredencialesSimpleDTO;
import farmacias.AppOchoa.dto.credencialesfel.CredencialesUpdateDTO;
import farmacias.AppOchoa.services.CredencialesService;
import farmacias.AppOchoa.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credenciales-fel/v1")
@AllArgsConstructor
public class CredencialesFelController extends BaseController {
    private final CredencialesService credencialesService;
    private final JwtUtil jwtUtil;

    @GetMapping("/{id}")
    public ResponseEntity<CredencialesResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(credencialesService.buscarPorId(getFarmaciaId(), id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<CredencialesSimpleDTO>> listar(){
        return ResponseEntity.ok(credencialesService.listar(getFarmaciaId()));
    }

    @PostMapping
    public ResponseEntity<CredencialesResponseDTO> crear(@RequestBody @Valid CredencialesCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(credencialesService.crear(getFarmaciaId(), dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CredencialesResponseDTO> actualizar(@PathVariable Long id, @RequestBody @Valid CredencialesUpdateDTO dto) {
        return ResponseEntity.ok(credencialesService.actualizar(getFarmaciaId(), id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        credencialesService.eliminar(getFarmaciaId(), id);
        return ResponseEntity.noContent().build();

    }
}