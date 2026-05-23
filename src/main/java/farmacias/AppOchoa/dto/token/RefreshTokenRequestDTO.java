package farmacias.AppOchoa.dto.token;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequestDTO {

    @NotBlank(message = "El refresh token es obligatorio")
    private String refreshToken;
}