package farmacias.AppOchoa.services;

import java.util.Map;

public interface AuthService {
    Map<String, Object> login(String nombreUsuario, String contrasena);
}