package farmacias.AppOchoa.dto.venta;

import farmacias.AppOchoa.dto.ventafel.VentaFelSimpleDTO;
import farmacias.AppOchoa.model.Venta;
import farmacias.AppOchoa.model.VentaFel;
import farmacias.AppOchoa.model.VentaFelEstado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VentaCobroResponseDTO {

    private Long ventaId;
    private String ventaUuid;
    private BigDecimal ventaTotal;
    private VentaFelEstado felEstado;
    private VentaFelSimpleDTO ventaFel;

    public static VentaCobroResponseDTO fromEntity(Venta venta, VentaFel ventaFel){
        return VentaCobroResponseDTO.builder()
                .ventaId(venta.getVentaId())
                .ventaUuid(venta.getVentaUuid())
                .ventaTotal(venta.getVentaTotal())
                .ventaFel(VentaFelSimpleDTO.fromEntity(ventaFel))
                .build();

    }

}
