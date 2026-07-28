package farmacias.AppOchoa.dto.ventafel;

import jakarta.validation.constraints.NotBlank;

public record AnulacionRequestDTO(@NotBlank String motivo) {


}
