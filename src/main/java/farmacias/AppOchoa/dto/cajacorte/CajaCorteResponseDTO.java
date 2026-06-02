package farmacias.AppOchoa.dto.cajacorte;

import farmacias.AppOchoa.dto.cajasesiones.CajaSesionesSimpleDTO;
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
public class CajaCorteResponseDTO {
    private Long corteId;
    private UsuarioSimpleDTO supervisor;
    private CajaSesionesSimpleDTO sesion;
    private BigDecimal corteTotalEfectivo;
    private BigDecimal corteTotalTarjetaCredito;
    private BigDecimal corteTotalTarjetaDebito;
    private BigDecimal corteTotalVentas;
    private LocalDateTime corteFecha;

    public static CajaCorteResponseDTO fromEntity(CajaCorte cajaCorte){
        return CajaCorteResponseDTO.builder()
                .corteId(cajaCorte.getCorteId())
                .supervisor(cajaCorte.getUsuario() != null ?
                        UsuarioSimpleDTO.fromEntity(cajaCorte.getUsuario()): null)
                .sesion(cajaCorte.getCajaSesiones() != null ?
                        CajaSesionesSimpleDTO.fromEntity(cajaCorte.getCajaSesiones()): null)
                .corteTotalEfectivo(cajaCorte.getCorteTotalEfectivo())
                .corteTotalTarjetaCredito(cajaCorte.getCorteTotalTarjetaCredito())
                .corteTotalTarjetaDebito(cajaCorte.getCorteTotalTarjetaDebito())
                .corteTotalVentas(cajaCorte.getCorteTotalVentas())
                .corteFecha(cajaCorte.getCorteFecha())
                .build();

    }
}
