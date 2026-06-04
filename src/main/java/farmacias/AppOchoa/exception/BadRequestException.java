package farmacias.AppOchoa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException{ //ERROR 400
    public BadRequestException(String mensaje){
        super(mensaje);
    }
}
