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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Código de Barras");
            header.createCell(1).setCellValue("Producto");
            header.createCell(2).setCellValue("Cantidad en Sistema");
            header.createCell(3).setCellValue("Conteo Físico");
            header.createCell(4).setCellValue("Diferencia");

            sheet.setColumnWidth(0, 8000);
            sheet.setColumnWidth(1, 5000);
            sheet.setColumnWidth(2, 4000);
            sheet.setColumnWidth(3, 4000);
            sheet.setColumnWidth(4, 4000);

            int rowNum = 1;
            for (Inventario item : inventario) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getProducto().getProductoCodigoBarras() != null
                        ? item.getProducto().getProductoCodigoBarras() : "");
                row.createCell(1).setCellValue(item.getProducto().getProductoNombre());
                row.createCell(2).setCellValue(item.getInventarioCantidadActual());
                row.createCell(3).setCellValue("");
                row.createCell(4).setCellValue("");
            }

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

        try (Workbook workbook = new SXSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventario");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Código de Barras");
            header.createCell(1).setCellValue("Producto");
            header.createCell(2).setCellValue("Precio Compra");
            header.createCell(3).setCellValue("Precio Venta");
            header.createCell(4).setCellValue("Fecha Vencimiento");
            header.createCell(5).setCellValue("Cantidad Actual");
            header.createCell(6).setCellValue("Cantidad Mínima");
            header.createCell(7).setCellValue("Categoría");

            sheet.setColumnWidth(0, 8000);
            sheet.setColumnWidth(1, 5000);
            sheet.setColumnWidth(2, 4000);
            sheet.setColumnWidth(3, 4000);
            sheet.setColumnWidth(4, 6000);
            sheet.setColumnWidth(5, 4000);
            sheet.setColumnWidth(6, 4000);
            sheet.setColumnWidth(7, 5000);

            int rowNum = 1;
            for (InventarioLotes item : inventarioLotes) {
                Inventario inv = item.getInventario();
                Integer cantidadActual = inv != null ? inv.getInventarioCantidadActual() : 0;
                Integer cantidadMinima = inv != null ? inv.getInventarioCantidadMinima() : 0;

                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getProducto().getProductoCodigoBarras() != null
                        ? item.getProducto().getProductoCodigoBarras() : "");
                row.createCell(1).setCellValue(item.getProducto().getProductoNombre());
                row.createCell(2).setCellValue("Q " + String.format("%.2f", item.getProducto().getProductoPrecioCompra().doubleValue()));
                row.createCell(3).setCellValue("Q " + String.format("%.2f", item.getProducto().getProductoPrecioVenta().doubleValue()));
                row.createCell(4).setCellValue(item.getLoteFechaVencimiento() != null
                        ? item.getLoteFechaVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
                row.createCell(5).setCellValue(cantidadActual);
                row.createCell(6).setCellValue(cantidadMinima);
                row.createCell(7).setCellValue(item.getProducto().getCategoria() != null
                        ? item.getProducto().getCategoria().getCategoriaNombre() : "");
            }

            long totalProductos = inventarioLotes.stream()
                    .map(i -> i.getProducto().getProductoId()).distinct().count();
            long totalCategorias = inventarioLotes.stream()
                    .filter(i -> i.getProducto().getCategoria() != null)
                    .map(i -> i.getProducto().getCategoria().getCategoriaId()).distinct().count();
            long totalUnidades = inventarioLotes.stream()
                    .mapToInt(InventarioLotes::getLoteCantidadActual).sum();
            double totalSinIVA = inventarioLotes.stream()
                    .mapToDouble(i -> i.getProducto().getProductoPrecioVenta().doubleValue()
                            * i.getLoteCantidadActual()).sum();
            double totalConIVA = inventarioLotes.stream()
                    .mapToDouble(i -> i.getProducto().getProductoPrecioVenta().doubleValue()
                            * (1 + (i.getProducto().getProductoIva() != null
                            ? i.getProducto().getProductoIva().doubleValue() : 0.0) / 100)
                            * i.getLoteCantidadActual()).sum();
            long vencidos = inventarioLotes.stream()
                    .filter(i -> i.getLoteFechaVencimiento() != null
                            && i.getLoteFechaVencimiento().isBefore(LocalDate.now())).count();
            long proximosAVencer = inventarioLotes.stream()
                    .filter(i -> i.getLoteFechaVencimiento() != null
                            && i.getLoteFechaVencimiento().isAfter(LocalDate.now())
                            && i.getLoteFechaVencimiento().isBefore(LocalDate.now().plusDays(30))).count();

            Sheet resumen = workbook.createSheet("Resumen");
            Row fila0 = resumen.createRow(0);
            fila0.createCell(0).setCellValue("Total Productos");
            fila0.createCell(1).setCellValue(totalProductos);
            Row fila1 = resumen.createRow(1);
            fila1.createCell(0).setCellValue("Total Categorías");
            fila1.createCell(1).setCellValue(totalCategorias);
            Row fila2 = resumen.createRow(2);
            fila2.createCell(0).setCellValue("Total Unidades");
            fila2.createCell(1).setCellValue(totalUnidades);
            Row fila3 = resumen.createRow(3);
            fila3.createCell(0).setCellValue("Total sin IVA");
            fila3.createCell(1).setCellValue("Q " + String.format("%.2f", totalSinIVA));
            Row fila4 = resumen.createRow(4);
            fila4.createCell(0).setCellValue("Total con IVA");
            fila4.createCell(1).setCellValue("Q " + String.format("%.2f", totalConIVA));
            Row fila5 = resumen.createRow(5);
            fila5.createCell(0).setCellValue("Vencidos");
            fila5.createCell(1).setCellValue(vencidos);
            Row fila6 = resumen.createRow(6);
            fila6.createCell(0).setCellValue("Próximos a Vencer (30 días)");
            fila6.createCell(1).setCellValue(proximosAVencer);

            httpServletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            httpServletResponse.setHeader("Content-Disposition", "attachment; filename=inventario.xlsx");
            workbook.write(httpServletResponse.getOutputStream());
        }
    }
}