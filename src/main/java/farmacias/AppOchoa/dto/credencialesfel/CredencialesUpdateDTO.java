package farmacias.AppOchoa.dto.credencialesfel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CredencialesUpdateDTO {

    @NotBlank(message = "El usuario es obligatorio")
    private String credencialUsuario;

    @NotBlank(message = "El secreto es obligatorio")
    private String credencialSecreto;

    private String credencialExtra;

    @NotNull(message = "El cliente es obligatorio")
    private Long credencialCliente;

    @NotNull(message = "El contrato es obligatorio")
    private Long credencialContrato;

    @NotNull(message = "La firma del emisor es obligatoria")
    private Boolean firmarEmisor;

    private Boolean validarIdentificador;
    private Boolean activa;
}
