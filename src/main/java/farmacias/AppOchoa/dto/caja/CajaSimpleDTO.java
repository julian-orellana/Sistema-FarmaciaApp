package farmacias.AppOchoa.dto.caja;

import farmacias.AppOchoa.dto.sucursal.SucursalSimpleDTO;
import farmacias.AppOchoa.model.Caja;
import farmacias.AppOchoa.model.CajaEstado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CajaSimpleDTO {
    private Long cajaId;
    private String cajaNombre;
    private CajaEstado cajaEstado;
    private SucursalSimpleDTO sucursal;

    public static CajaSimpleDTO fromEntity(Caja caja){
        return CajaSimpleDTO.builder()
                .cajaId(caja.getCajaId())
                .cajaNombre(caja.getCajaNombre())
                .cajaEstado(caja.getCajaEstado())
                .sucursal(caja.getSucursal() != null ?
                        SucursalSimpleDTO.fromEntity(caja.getSucursal()) : null)
                .build();
    }

}
