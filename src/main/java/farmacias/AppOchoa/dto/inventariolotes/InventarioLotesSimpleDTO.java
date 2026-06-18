package farmacias.AppOchoa.dto.inventariolotes;

import farmacias.AppOchoa.model.Inventario;
import farmacias.AppOchoa.model.InventarioLotes;
import farmacias.AppOchoa.model.LoteEstado;
import lombok.*;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventarioLotesSimpleDTO {

    private Long loteId;
    private String numeroLote;
    private LocalDate fechaVencimiento;
    private Integer cantidadActual;
    private LoteEstado estado;
    private Long productoId;
    private String productoNombre;
    private Integer cantidadInicial;
    private BigDecimal precioCompra;

    public static InventarioLotesSimpleDTO fromEntity(InventarioLotes lote) {
        return InventarioLotesSimpleDTO.builder()
                .loteId(lote.getLoteId())
                .numeroLote(lote.getLoteNumero())
                .fechaVencimiento(lote.getLoteFechaVencimiento())
                .cantidadActual(lote.getLoteCantidadActual())
                .estado(lote.getLoteEstado())
                .productoId(lote.getProducto().getProductoId())
                .productoNombre(lote.getProducto().getProductoNombre())
                .cantidadInicial(lote.getLoteCantidadInicial())
                .precioCompra(lote.getLotePrecioCompra())
                .build();
    }
}

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
            for (
Inventario item : inventario) {
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