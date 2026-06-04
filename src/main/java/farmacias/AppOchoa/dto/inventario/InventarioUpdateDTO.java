package farmacias.AppOchoa.dto.inventario;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class InventarioUpdateDTO {

    @NotNull(message = "La cantidad actual es obligatoria")
    @Min(value = 0, message = "La cantidad actual no puede ser negativa")
    private Integer cantidadActual;

    @NotNull(message = "La cantidad minima es obligatoria")
    @Min(value = 1, message = "La cantidad minima debe ser al menos 1")
    private Integer cantidadMinima;


}
