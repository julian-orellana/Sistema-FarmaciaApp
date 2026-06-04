package farmacias.AppOchoa.dto.categoria;

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

public class CategoriaCreateDTO {

    @NotBlank(message = "El nombre de la categoria debe ser obligatorio")
    @Size(max = 100, message = "El nombre de la categoria no debe de tener mas de 100 caracteres")
    private String nombre;
}
