package farmacias.AppOchoa.controller;

import farmacias.AppOchoa.services.ExcelService;
import farmacias.AppOchoa.util.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/excel")
@AllArgsConstructor
@Tag(name = "Excel-Controller")
public class ExcelController extends BaseController {
    private final JwtUtil jwtUtil;
    private final ExcelService excelService;

    @PreAuthorize("hasAuthority('administrador')")
    @GetMapping("inventario/exportar")
    public void exportarInventario(
            @RequestParam Long farmaciaId,
            @RequestParam(required = false) Long sucursalId,
            @RequestParam(required = false) Long categoriaId,
            HttpServletResponse response) throws Exception{
        excelService.exportarInventario(farmaciaId, sucursalId, categoriaId, response);
    }

    @PreAuthorize("hasAuthority('administrador')")
    @GetMapping("inventario/completo")
    public void inventarioCompleto(
            @RequestParam Long farmaciaId,
            @RequestParam(required = false) Long sucursalId,
            @RequestParam(required = false) Long categoriaId,
            HttpServletResponse response) throws Exception {
        excelService.inventarioCompleto(farmaciaId, sucursalId, categoriaId, response);
    }
}
