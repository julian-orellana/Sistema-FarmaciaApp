package farmacias.AppOchoa.dto.autorizacion;

import farmacias.AppOchoa.model.AutorizacionTipo;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AutorizacionCreateDTO {
    @NotNull(message = "La referencia es obligatoria")
    private Long autorizacionReferenciaId;
    @NotNull(message = "El ID del cajero es obligatorio")
    private Long cajeroId;
    @NotNull(message = "El ID del supervisor es obligatorio")
    private Long supervisorId;
    @NotNull(message = "El tipo de autorización es obligatorio")
    private AutorizacionTipo autorizacionTipo;

}
