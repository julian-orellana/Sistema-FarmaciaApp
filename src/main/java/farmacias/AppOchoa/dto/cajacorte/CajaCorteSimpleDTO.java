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
public class CajaCorteSimpleDTO {
    private Long corteId;
    private UsuarioSimpleDTO usuarioNombre;
    private UsuarioSimpleDTO usuarioRol;
    private String sucursalNombre;
    private BigDecimal corteTotalTarjetaCredito;
    private BigDecimal corteTotalEfectivo;
    private BigDecimal corteTotalTarjetaDebito;
    private BigDecimal corteTotalVentas;
    private LocalDateTime corteFecha;
    private BigDecimal sesionFondoInicial;
    private LocalDateTime sesionFechaApertura;
    private LocalDateTime sesionFechaCierre;


    public static CajaCorteSimpleDTO fromEntity(CajaCorte cajaCorte) {
        return CajaCorteSimpleDTO.builder()
                .corteId(cajaCorte.getCorteId())
                .usuarioNombre(cajaCorte.getUsuario() != null ?
                        UsuarioSimpleDTO.fromEntity(cajaCorte.getUsuario()) : null)
                .usuarioRol(cajaCorte.getUsuario() != null?
                        UsuarioSimpleDTO.fromEntity(cajaCorte.getUsuario()): null)
                .corteTotalVentas(cajaCorte.getCorteTotalVentas())
                .corteFecha(cajaCorte.getCorteFecha())
                .corteTotalEfectivo(cajaCorte.getCorteTotalEfectivo())
                .corteTotalTarjetaCredito(cajaCorte.getCorteTotalTarjetaCredito())
                .corteTotalTarjetaDebito(cajaCorte.getCorteTotalTarjetaDebito())
                .sesionFondoInicial(cajaCorte.getCajaSesiones().getSesionFondoInicial())
                .sesionFechaApertura(cajaCorte.getCajaSesiones().getSesionFechaApertura())
                .sesionFechaCierre(cajaCorte.getCajaSesiones().getSesionFechaCierre())
                .build();
    }
}