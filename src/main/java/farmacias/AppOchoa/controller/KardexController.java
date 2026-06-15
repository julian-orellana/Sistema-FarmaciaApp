package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.kardex.KardexResponseDTO;
import farmacias.AppOchoa.services.KardexService;
import farmacias.AppOchoa.util.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kardex")
@AllArgsConstructor
@Tag(name = "kardex-controller")
public class KardexController extends  BaseController {
    private final KardexService kardexService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<List<KardexResponseDTO>> obtenerKardex(
            @RequestParam Long productoId,
            @RequestParam Long farmaciaId){
        return ResponseEntity.ok(kardexService.obtenerKardex(productoId, farmaciaId));
    }
}
