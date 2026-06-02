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
public class CajaSesionesResponseDTO {
    private Long sesionId;
    private UsuarioSimpleDTO usuario;
    private CajaSimpleDTO caja;
    private LocalDateTime sesionFechaApertura;
    private LocalDateTime sesionFechaCierre;
    private BigDecimal sesionFondoInicial;
    private SesionEstado sesionEstado;

    public static CajaSesionesResponseDTO fromEntity(CajaSesiones cajaSesiones) {
        return CajaSesionesResponseDTO.builder()
                .sesionId(cajaSesiones.getSesionId())
                .usuario(cajaSesiones.getUsuario() != null ?
                        UsuarioSimpleDTO.fromEntity(cajaSesiones.getUsuario()) : null)
                .caja(cajaSesiones.getCaja() != null ?
                        CajaSimpleDTO.fromEntity(cajaSesiones.getCaja()) : null)
                .sesionFechaApertura(cajaSesiones.getSesionFechaApertura())
                .sesionFechaCierre(cajaSesiones.getSesionFechaCierre())
                .sesionFondoInicial(cajaSesiones.getSesionFondoInicial())
                .sesionEstado(cajaSesiones.getSesionEstado())
                .build();
    }
}