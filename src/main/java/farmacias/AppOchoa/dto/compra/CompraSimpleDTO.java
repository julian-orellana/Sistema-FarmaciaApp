package farmacias.AppOchoa.dto.compra;

import farmacias.AppOchoa.model.Compra;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CompraSimpleDTO {

    private Long compraId;
    private String nombreSucursal;
    private String nombreUsuario;
    private LocalDate compraFecha;
    private BigDecimal compraTotal;

    public static CompraSimpleDTO fromEntity(Compra compra){
        return CompraSimpleDTO.builder()
                .compraId(compra.getCompraId())
                .nombreSucursal(compra.getSucursal() != null ?
                        compra.getSucursal().getSucursalNombre() : null)
                .nombreUsuario(compra.getUsuario() != null ?
                        compra.getUsuario().getUsuarioNombre() + " " +
                                compra.getUsuario().getUsuarioApellido() : null)
                .compraFecha(compra.getCompraFecha())
                .compraTotal(compra.getCompraTotal())
                .build();





    }




}
