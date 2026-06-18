package farmacias.AppOchoa.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductoVendidoDTO {

    private Long productoId;
    private String productoNombre;
    private Long totalUnidades;
}
