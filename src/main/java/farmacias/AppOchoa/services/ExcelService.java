package farmacias.AppOchoa.services;

import jakarta.servlet.http.HttpServletResponse;

public interface ExcelService {
    void exportarInventario(Long farmaciaId, Long sucursalId, Long categoriaId, HttpServletResponse response) throws Exception;
}
