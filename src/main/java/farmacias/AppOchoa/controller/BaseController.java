package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.exception.BadRequestException;
import farmacias.AppOchoa.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private HttpServletRequest request;

    protected Long getFarmaciaId() {
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer")){
            throw new BadRequestException("Authorization header invalido");
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractFarmaciaId(token);
    }
}