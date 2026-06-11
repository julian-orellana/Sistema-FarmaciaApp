package farmacias.AppOchoa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class SuscripcionVencidaException extends RuntimeException { //ERROR 401
    public SuscripcionVencidaException(String mensaje) {
        super(mensaje);
    }
}
