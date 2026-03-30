package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.farmacia.FarmaciaCreateDTO;
import farmacias.AppOchoa.dto.farmacia.FarmaciaResponseDTO;
import farmacias.AppOchoa.dto.farmacia.FarmaciaSimpleDTO;
import farmacias.AppOchoa.services.FarmaciaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/farmacias")
@AllArgsConstructor

public class FarmaciaController extends  BaseController{
    private final FarmaciaService farmaciaService;

    @GetMapping
    public ResponseEntity<Page<FarmaciaSimpleDTO>> listarFarmacias(
            @PageableDefault(size = 10 , sort = "farmaciaNombre")Pageable pageable){
        Page<FarmaciaSimpleDTO> farmacias = farmaciaService.listarFarmacias(pageable);
        return ResponseEntity.ok(farmacias);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<FarmaciaResponseDTO> buscarId(@PathVariable Long id){
        FarmaciaResponseDTO farmaciaResponseDTO = farmaciaService.buscarId(id);
        return ResponseEntity.ok(farmaciaResponseDTO);
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<FarmaciaSimpleDTO>> buscarPorTexto(@RequestParam String texto, Pageable pageable){
        return ResponseEntity.ok(farmaciaService.buscarPorTexto(texto, pageable));
    }

    @PostMapping
    public ResponseEntity<FarmaciaResponseDTO> crear(@Valid @RequestBody FarmaciaCreateDTO dto){
        FarmaciaResponseDTO farmaciaResponseDTO = farmaciaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(farmaciaResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        farmaciaService.eliminar(id);
        return ResponseEntity.noContent().build();

    }



}
