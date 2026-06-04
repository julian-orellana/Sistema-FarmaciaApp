package farmacias.AppOchoa.dto.compra;

import farmacias.AppOchoa.dto.compradetalle.CompraDetalleCreateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompraCreateDTO {


    @NotNull(message = "La sucursal es obligatoria")
    private Long sucursalId;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "La fecha de compra es obligatoria")
    private LocalDate fechaCompra;

    @Size(max = 65535, message = "Las observaciones no deben exceder 65535 caracteres")
    private String observaciones;

    @NotEmpty(message = "Debe incluir al menos un detalle de compra")
    @Valid
    private List<CompraDetalleCreateDTO> detalles;
}