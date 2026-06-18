package farmacias.AppOchoa.dto.inventario;

import farmacias.AppOchoa.dto.producto.ProductoSimpleDTO;
import farmacias.AppOchoa.dto.sucursal.SucursalSimpleDTO;
import farmacias.AppOchoa.model.Inventario;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventarioResponseDTO {

    private Long inventarioId;
    private ProductoSimpleDTO producto;
    private SucursalSimpleDTO sucursal;
    private Integer cantidadActual;
    private Integer cantidadMinima;
    private String productoNombre;
    private String productoCodigoBarras;
    private BigDecimal productoPrecioCompra;
    private BigDecimal productoPrecioVenta;

    public static InventarioResponseDTO fromEntity(Inventario inventario) {
        return InventarioResponseDTO.builder()
                .inventarioId(inventario.getInventarioId())
                .producto(inventario.getProducto() != null ?
                        ProductoSimpleDTO.fromEntity(inventario.getProducto()) : null)
                .sucursal(inventario.getSucursal() != null ?
                        SucursalSimpleDTO.fromEntity(inventario.getSucursal()) : null)
                .cantidadActual(inventario.getInventarioCantidadActual())
                .cantidadMinima(inventario.getInventarioCantidadMinima())
                .productoNombre(inventario.getProducto().getProductoNombre())
                .productoNombre(inventario.getProducto().getProductoNombre())
                .productoCodigoBarras(inventario.getProducto().getProductoCodigoBarras())
                .productoPrecioCompra(inventario.getProducto().getProductoPrecioCompra())
                .productoPrecioVenta(inventario.getProducto().getProductoPrecioVenta())
                .build();
    }
}