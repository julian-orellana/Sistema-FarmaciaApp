package farmacias.AppOchoa.dto.cajacorte;

import farmacias.AppOchoa.dto.usuario.UsuarioSimpleDTO;
import farmacias.AppOchoa.model.CajaCorte;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CajaCorteSimpleDTO {
    private Long corteId;
    private UsuarioSimpleDTO supervisor;
    private BigDecimal corteTotalVentas;
    private LocalDateTime corteFecha;

    public static CajaCorteSimpleDTO fromEntity(CajaCorte cajaCorte) {
        return CajaCorteSimpleDTO.builder()
                .corteId(cajaCorte.getCorteId())
                .supervisor(cajaCorte.getUsuario() != null ?
                        UsuarioSimpleDTO.fromEntity(cajaCorte.getUsuario()) : null)
                .corteTotalVentas(cajaCorte.getCorteTotalVentas())
                .corteFecha(cajaCorte.getCorteFecha())
                .build();
    }
}