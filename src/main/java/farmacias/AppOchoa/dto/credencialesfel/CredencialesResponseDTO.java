package farmacias.AppOchoa.dto.credencialesfel;

import farmacias.AppOchoa.dto.sucursal.SucursalSimpleDTO;
import farmacias.AppOchoa.model.CredencialesFel;
import farmacias.AppOchoa.model.Sucursal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CredencialesResponseDTO {

    private Long credencialId;
    private String ambiente;
    private Boolean activa;
    private String certificador;
    private LocalDateTime fechaValidacion;
    private LocalDateTime auditoriaFechaCreacion;
    private SucursalSimpleDTO sucursal;

    public static CredencialesResponseDTO fromEntity(CredencialesFel credencialesFel){
        return CredencialesResponseDTO.builder()
                .credencialId(credencialesFel.getCredencialId())
                .ambiente(credencialesFel.getAmbiente())
                .activa(credencialesFel.getActiva())
                .certificador(credencialesFel.getCertificador())
                .fechaValidacion(credencialesFel.getFechaValidacion())
                .auditoriaFechaCreacion(credencialesFel.getAuditoriaFechaCreacion())
                .sucursal(credencialesFel.getSucursal() != null ?
                        SucursalSimpleDTO.fromEntity(credencialesFel.getSucursal()): null)
                .build();
    }
}
