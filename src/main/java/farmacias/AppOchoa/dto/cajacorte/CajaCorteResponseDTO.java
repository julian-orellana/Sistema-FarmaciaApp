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


    public static CajaCorteResponseDTO fromEntity(CajaCorte cajaCorte) {
        return CajaCorteResponseDTO.builder()
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
                .sesionFondoInicial(cajaCorte.getCajaSesiones() != null ?
                        cajaCorte.getCajaSesiones().getSesionFondoInicial() : null)
                .sesionFechaApertura(cajaCorte.getCajaSesiones() != null ?
                        cajaCorte.getCajaSesiones().getSesionFechaApertura() : null)
                .sesionFechaCierre(cajaCorte.getCajaSesiones() != null ?
                        cajaCorte.getCajaSesiones().getSesionFechaCierre() : null)
                .sucursalNombre(cajaCorte.getCajaSesiones() != null
                        && cajaCorte.getCajaSesiones().getCaja() != null
                        && cajaCorte.getCajaSesiones().getCaja().getSucursal() != null
                        ? cajaCorte.getCajaSesiones().getCaja().getSucursal().getSucursalNombre() : null)
                .build();
    }
}
