package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.model.Inventario;
import farmacias.AppOchoa.repository.InventarioRepository;
import farmacias.AppOchoa.services.ExcelService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExcelServiceImpl implements ExcelService {

    private final InventarioRepository inventarioRepository;

    public ExcelServiceImpl(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void exportarInventario(Long farmaciaId, Long sucursalId, Long categoriaId, HttpServletResponse response) throws Exception {

        List<Inventario> inventario;

        if (sucursalId != null && categoriaId != null) {
            inventario = inventarioRepository
                    .findByFarmacia_FarmaciaIdAndSucursal_SucursalIdAndProducto_Categoria_CategoriaId(
                            farmaciaId, sucursalId, categoriaId);
        } else if (sucursalId != null) {
            inventario = inventarioRepository
                    .findByFarmacia_FarmaciaIdAndSucursal_SucursalId(farmaciaId, sucursalId);
        } else {
            inventario = inventarioRepository.findByFarmacia_FarmaciaId(farmaciaId);
        }

        try (Workbook workbook = new SXSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventario");

            // Encabezado
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Producto");
            header.createCell(1).setCellValue("Cantidad en Sistema");
            header.createCell(2).setCellValue("Conteo Físico");
            header.createCell(3).setCellValue("Diferencia");
            header.createCell(4).setCellValue("Precio Compra");
            header.createCell(5).setCellValue("Código de Barras");

            // Anchos de columna
            sheet.setColumnWidth(0, 8000);
            sheet.setColumnWidth(1, 5000);
            sheet.setColumnWidth(2, 4000);
            sheet.setColumnWidth(3, 4000);
            sheet.setColumnWidth(4, 4000);
            sheet.setColumnWidth(5, 4000);

            // Data
            int rowNum = 1;
            for (Inventario item : inventario) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getProducto().getProductoNombre());
                row.createCell(1).setCellValue(item.getInventarioCantidadActual());
                row.createCell(2).setCellValue("");
                row.createCell(3).setCellValue("");
                row.createCell(4).setCellValue(item.getProducto().getProductoPrecioCompra().doubleValue());
                row.createCell(5).setCellValue(item.getProducto().getProductoCodigoBarras() != null
                        ? item.getProducto().getProductoCodigoBarras() : "");
            }

            // Respuesta HTTP
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=inventario.xlsx");

            workbook.write(response.getOutputStream());
        }
    }
}