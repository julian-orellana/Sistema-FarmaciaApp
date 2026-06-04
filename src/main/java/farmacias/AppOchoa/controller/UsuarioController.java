package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.dto.usuario.*;
import farmacias.AppOchoa.services.UsuarioService;
import farmacias.AppOchoa.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@AllArgsConstructor
public class UsuarioController extends BaseController {
    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    @Autowired
    private HttpServletRequest request;


    @GetMapping
    public ResponseEntity<Page<UsuarioSimpleDTO>> listarUsuarioPaginados(
            @PageableDefault(size = 10, sort = "usuarioNombre") Pageable pageable){
        return ResponseEntity.ok(usuarioService.listarUsuariosActivosPaginado(getFarmaciaId(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(usuarioService.obtenerPorId(getFarmaciaId(), id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<UsuarioSimpleDTO>> buscarPorTexto(
            @RequestParam String texto, Pageable pageable){
        return ResponseEntity.ok(usuarioService.buscarPorTexto(getFarmaciaId(), texto, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@Valid @RequestBody UsuarioCreateDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crearUsuario(getFarmaciaId(), dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
            @PathVariable Long id, @Valid @RequestBody UsuarioUpdateDTO dto){
        return ResponseEntity.ok(usuarioService.actualizarUsuario(getFarmaciaId(), id, dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestParam Boolean estado){
        usuarioService.cambiarEstado(getFarmaciaId(), id, estado);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id){
        usuarioService.eliminarUsuario(getFarmaciaId(), id);
        return ResponseEntity.noContent().build();
    }

}