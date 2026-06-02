package farmacias.AppOchoa.dto.caja;

import farmacias.AppOchoa.dto.sucursal.SucursalSimpleDTO;
import farmacias.AppOchoa.model.Caja;
import farmacias.AppOchoa.model.CajaEstado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CajaResponseDTO {
    private Long cajaId;
    private String cajaNombre;
    private CajaEstado cajaEstado;
    private SucursalSimpleDTO sucursal;

    public static CajaResponseDTO fromEntity (Caja caja){
        return CajaResponseDTO.builder()
                .cajaId(caja.getCajaId())
                .cajaNombre(caja.getCajaNombre())
                .cajaEstado(caja.getCajaEstado())
                .sucursal(caja.getSucursal() != null ?
                        SucursalSimpleDTO.fromEntity(caja.getSucursal()) : null)
                .build();

    }
}
