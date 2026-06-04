package farmacias.AppOchoa.dto.inventario;

import farmacias.AppOchoa.dto.producto.ProductoSimpleDTO;
import farmacias.AppOchoa.dto.sucursal.SucursalSimpleDTO;
import farmacias.AppOchoa.model.Inventario;
import lombok.*;

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

    public static InventarioResponseDTO fromEntity(Inventario inventario) {
        return InventarioResponseDTO.builder()
                .inventarioId(inventario.getInventarioId())
                .producto(inventario.getProducto() != null ?
                        ProductoSimpleDTO.fromEntity(inventario.getProducto()) : null)
                .sucursal(inventario.getSucursal() != null ?
                        SucursalSimpleDTO.fromEntity(inventario.getSucursal()) : null)
                .cantidadActual(inventario.getInventarioCantidadActual())
                .cantidadMinima(inventario.getInventarioCantidadMinima())
                .build();
    }
}