package farmacias.AppOchoa.dto.inventariolotes;

import farmacias.AppOchoa.model.LoteEstado;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventarioLotesUpdateDTO {

    @NotNull(message = "La cantidad actual es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidadActual;

    @NotNull(message = "La cantidad minima es obligatoria")
    private Integer cantidadMinima;

    // AGREGA ESTO:
    @NotNull(message = "El estado es obligatorio")
    private LoteEstado estado;
}