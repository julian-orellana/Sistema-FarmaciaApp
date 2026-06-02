package farmacias.AppOchoa.dto.compra;

import farmacias.AppOchoa.dto.compradetalle.CompraDetalleCreateDTO;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CompraUpdateDTO {


    @Size(max = 65535, message = "Las observaciones no deben exceder 65535 caracteres")
    private String observaciones;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;


}
