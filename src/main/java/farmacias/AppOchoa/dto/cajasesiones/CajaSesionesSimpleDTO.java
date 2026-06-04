package farmacias.AppOchoa.dto.cajasesiones;

import farmacias.AppOchoa.dto.caja.CajaSimpleDTO;
import farmacias.AppOchoa.dto.usuario.UsuarioSimpleDTO;
import farmacias.AppOchoa.model.CajaSesiones;
import farmacias.AppOchoa.model.SesionEstado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CajaSesionesSimpleDTO {
    private Long sesionId;
    private CajaSimpleDTO caja;
    private SesionEstado sesionEstado;

    public static CajaSesionesSimpleDTO fromEntity(CajaSesiones cajaSesiones) {
        return CajaSesionesSimpleDTO.builder()
                .sesionId(cajaSesiones.getSesionId())
                .caja(cajaSesiones.getCaja() != null ?
                        CajaSimpleDTO.fromEntity(cajaSesiones.getCaja()) : null)
                .sesionEstado(cajaSesiones.getSesionEstado())
                .build();
    }
}



