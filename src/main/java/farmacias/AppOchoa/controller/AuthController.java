package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String nombreUsuario = credentials.get("nombreUsuario");
            String contrasena = credentials.get("contrasena");

            //Validar que vengan los datos
            if (nombreUsuario == null || contrasena == null) {
                Map<String, String> error = new HashMap<>();
                error.put("mensaje", "Nombre de usuario y contraseña son obligatorios");
                return ResponseEntity.badRequest().body(error);
            }

            //Llamar al servicio de autenticación
            Map<String, Object> response = authService.login(nombreUsuario, contrasena);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);

            //Agregation
        }
    }
}