package farmacias.AppOchoa.services;

import farmacias.AppOchoa.model.Inventario;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.List;

public interface ExcelService {
    void exportarInventario(Long farmaciaId, Long sucursalId, Long categoriaId, HttpServletResponse response) throws Exception;
    void inventarioCompleto(Long farmaciaId, Long sucursalId, Long categoriaId, HttpServletResponse response) throws  Exception;
}

