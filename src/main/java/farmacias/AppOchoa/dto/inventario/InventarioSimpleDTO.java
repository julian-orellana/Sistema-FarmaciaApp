package farmacias.AppOchoa.dto.inventario;

import farmacias.AppOchoa.dto.sucursal.SucursalSimpleDTO;
import farmacias.AppOchoa.model.Inventario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class InventarioSimpleDTO {
    private Long inventarioId;
    private Integer cantidadActual;
    private Integer cantidadMinima;
    private SucursalSimpleDTO sucursal;
    private Long productoId;
    private String productoNombre;
    private String productoCodigoBarras;
    private BigDecimal productoPrecioCompra;
    private BigDecimal productoPrecioVenta;


    public static InventarioSimpleDTO fromEntity(Inventario inventario) {
        return InventarioSimpleDTO.builder()
                .inventarioId(inventario.getInventarioId())
                .cantidadActual(inventario.getInventarioCantidadActual())
                .cantidadMinima(inventario.getInventarioCantidadMinima())
                .productoId(inventario.getProducto().getProductoId())
                .productoNombre(inventario.getProducto().getProductoNombre())
                .productoNombre(inventario.getProducto().getProductoNombre())
                .productoCodigoBarras(inventario.getProducto().getProductoCodigoBarras())
                .productoPrecioCompra(inventario.getProducto().getProductoPrecioCompra())
                .productoPrecioVenta(inventario.getProducto().getProductoPrecioVenta())
                .sucursal(inventario.getSucursal() != null ?
                        SucursalSimpleDTO.fromEntity(inventario.getSucursal()) : null)
                .build();
    }
}