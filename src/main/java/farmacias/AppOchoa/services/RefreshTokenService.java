package farmacias.AppOchoa.services;

import farmacias.AppOchoa.model.RefreshToken;

public interface RefreshTokenService {

    RefreshToken crear(Long usuarioId);
    RefreshToken verificarYRotar(String tokenStr);
    void verificarParaLogout(String tokenStr);
    void revocarPorUsuario(Long usuarioId);
}
