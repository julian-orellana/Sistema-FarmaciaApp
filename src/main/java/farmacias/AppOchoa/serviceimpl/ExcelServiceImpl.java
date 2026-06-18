package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.model.Inventario;
import farmacias.AppOchoa.model.InventarioLotes;
import farmacias.AppOchoa.repository.InventarioLotesRepository;
import farmacias.AppOchoa.repository.InventarioRepository;
import farmacias.AppOchoa.services.ExcelService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExcelServiceImpl implements ExcelService {

    private final InventarioRepository inventarioRepository;
    private final InventarioLotesRepository inventarioLotesRepository;

    public ExcelServiceImpl(InventarioRepository inventarioRepository,
                            InventarioLotesRepository inventarioLotesRepository) {
        this.inventarioRepository = inventarioRepository;
        this.inventarioLotesRepository = inventarioLotesRepository;
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

    @Override
    @Transactional(readOnly = true)
    public void inventarioCompleto(Long farmaciaId, Long sucursalId, Long categoriaId, HttpServletResponse httpServletResponse) throws Exception {
        List<InventarioLotes> inventarioLotes;

        if (sucursalId != null && categoriaId != null) {
            inventarioLotes = inventarioLotesRepository
                    .findByFarmacia_FarmaciaIdAndSucursal_SucursalIdAndProducto_Categoria_CategoriaId(
                            farmaciaId, sucursalId, categoriaId);
        } else if (sucursalId != null) {
            inventarioLotes = inventarioLotesRepository
                    .findByFarmacia_FarmaciaIdAndSucursal_SucursalId(farmaciaId, sucursalId);
        } else {
            inventarioLotes = inventarioLotesRepository.findByFarmacia_FarmaciaId(farmaciaId);
        }

        Sheet sheet;
        try (Workbook workbook = new SXSSFWorkbook()) {
            sheet = workbook.createSheet("Inventario");


            Row header = sheet.createRow(0);

            header.createCell(0).setCellValue("ProductoId");
            header.createCell(1).setCellValue("Código de Barras");
            header.createCell(2).setCellValue("Producto");
            header.createCell(3).setCellValue("Precio Compra");
            header.createCell(4).setCellValue("Precio Venta");
            header.createCell(5).setCellValue("Fecha Vencimiento");
            header.createCell(6).setCellValue("Cantidad Actual");
            header.createCell(7).setCellValue("Cantidad Minima");
            header.createCell(8).setCellValue("Categoria");

            sheet.setColumnWidth(0, 8000);
            sheet.setColumnWidth(1, 5000);
            sheet.setColumnWidth(2, 4000);
            sheet.setColumnWidth(3, 4000);
            sheet.setColumnWidth(4, 4000);
            sheet.setColumnWidth(5, 4000);
            sheet.setColumnWidth(6, 8000);
            sheet.setColumnWidth(7, 5000);
            sheet.setColumnWidth(8, 4000);
            sheet.setColumnWidth(9, 4000);


            int rowNum = 1;
            for (InventarioLotes item : inventarioLotes) {

                Optional<Inventario> inventarioOpt = inventarioRepository
                        .findByProducto_ProductoIdAndSucursal_SucursalId(
                                item.getProducto().getProductoId(),
                                item.getSucursal().getSucursalId());

                Integer cantidadActual = inventarioOpt.map(Inventario::getInventarioCantidadActual).orElse(null);
                Integer cantidadMinima = inventarioOpt.map(Inventario::getInventarioCantidadMinima).orElse(null);
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getProducto().getProductoId());
                row.createCell(1).setCellValue(item.getProducto().getProductoCodigoBarras());
                row.createCell(2).setCellValue(item.getProducto().getProductoNombre());
                row.createCell(3).setCellValue(item.getProducto().getProductoPrecioCompra().doubleValue());
                row.createCell(4).setCellValue(item.getProducto().getProductoPrecioVenta().doubleValue());
                row.createCell(5).setCellValue(item.getLoteFechaVencimiento().toString());
                row.createCell(6).setCellValue(cantidadActual != null ? cantidadActual : 0);
                row.createCell(7).setCellValue(cantidadMinima != null ? cantidadMinima : 0);
                row.createCell(8).setCellValue(item.getProducto().getCategoria().getCategoriaNombre());

            }

            // Respuesta HTTP
            httpServletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            httpServletResponse.setHeader("Content-Disposition", "attachment; filename=inventario.xlsx");

            workbook.write(httpServletResponse.getOutputStream());
        }
    }

    }