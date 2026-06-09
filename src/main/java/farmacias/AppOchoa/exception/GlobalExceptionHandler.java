package farmacias.AppOchoa.exception;

import farmacias.AppOchoa.exception.TokenRefreshException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<Object> manejarTokenRefresh(TokenRefreshException ex, WebRequest request) {
        return construirRespuesta(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> manejarCredencialesIncorrectas(BadCredentialsException ex, WebRequest request) {
        return construirRespuesta(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos", request);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Object> manejarUsuarioDesactivado(DisabledException ex, WebRequest request) {
        return construirRespuesta(HttpStatus.UNAUTHORIZED, "La cuenta de usuario está desactivada", request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> manejarBadRequest(BadRequestException ex, WebRequest request) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> manejarValidaciones(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("estado", HttpStatus.BAD_REQUEST.value());
        body.put("mensaje", "Error de validación en los datos iados");
        body.put("ruta", request.getDescription(false).replace("uri=", ""));

        Map<String, String> erroresCampos = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            erroresCampos.put(error.getField(), error.getDefaultMessage());
        }
        body.put("errores", erroresCampos);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> manejarIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> manejarAccesoDenegado(AccessDeniedException ex, WebRequest request) {
        return construirRespuesta(HttpStatus.FORBIDDEN, "No tienes permisos para acceder a este recurso", request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> manejarNoEncontrado(ResourceNotFoundException ex, WebRequest request) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<Object> manejarOperacionNoPermitida(UnsupportedOperationException ex, WebRequest request) {
        return construirRespuesta(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Object> manejarDuplicado(DuplicateResourceException ex, WebRequest request) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> manejarViolacionIntegridad(DataIntegrityViolationException ex, WebRequest request) {
        return construirRespuesta(
                HttpStatus.CONFLICT,
                "El registro ya existe o viola una restricción de unicidad en la base de datos",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> manejarExcepcionGlobal(Exception ex, WebRequest request) {
        log.error("Error 500 en {}: {}", request.getDescription(false), ex.getMessage(), ex);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("estado", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("mensaje", "Ocurrió un error interno. Contacta al administrador del sistema.");
        body.put("ruta", request.getDescription(false).replace("uri=", ""));
        // No exponer ex.getMessage() en producción — solo loguearlo
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private ResponseEntity<Object> construirRespuesta(HttpStatus status, String mensaje, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("estado", status.value());
        body.put("mensaje", mensaje);
        body.put("ruta", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, status);
    }
}