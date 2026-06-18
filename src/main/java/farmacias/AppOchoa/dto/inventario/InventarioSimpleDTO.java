package farmacias.AppOchoa.dto.inventario;

import farmacias.AppOchoa.model.Inventario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class InventarioSimpleDTO {
    private Long inventarioId;
    private Integer cantidadActual;
    private Integer cantidadMinima;
    private Long productoId;
    private String productoNombre;

    public static InventarioSimpleDTO fromEntity (Inventario inventario){
        return InventarioSimpleDTO.builder()
                .inventarioId(inventario.getInventarioId())
                .cantidadActual(inventario.getInventarioCantidadActual())
                .cantidadMinima(inventario.getInventarioCantidadMinima())
                .productoId(inventario.getProducto().getProductoId())
                .productoNombre(inventario.getProducto().getProductoNombre())
                .build();
    }
}
