package farmacias.AppOchoa.dto.venta;

import farmacias.AppOchoa.model.VentaEstado;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VentaUpdateDTO {

    @NotNull(message = "El estado es obligatorio")
    private VentaEstado estado;

    @NotNull(message = "El nombre del cliente es obligatorio")
    private String nombreCliente;

    @NotNull(message = "El nit del cliente es obligatorio")
    private String nitCliente;


}