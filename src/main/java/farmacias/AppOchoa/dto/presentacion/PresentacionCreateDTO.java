package farmacias.AppOchoa.dto.presentacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PresentacionCreateDTO {


    @NotBlank(message = "La descripcion de la presentacion es obligatoria")
    @Size(max = 50, message = "La descripcion no debe contener mas de 50 caracteres")
    private String nombre;
}
