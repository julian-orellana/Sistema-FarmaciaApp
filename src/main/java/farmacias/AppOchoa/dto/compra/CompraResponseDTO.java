package farmacias.AppOchoa.dto.compra;

import farmacias.AppOchoa.dto.compradetalle.CompraDetalleResponseDTO;
import farmacias.AppOchoa.dto.sucursal.SucursalSimpleDTO;
import farmacias.AppOchoa.dto.usuario.UsuarioSimpleDTO;
import farmacias.AppOchoa.model.CompraEstado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CompraResponseDTO {

    private Long compraId;
    private SucursalSimpleDTO sucursal;
    private UsuarioSimpleDTO usuario;
    private LocalDate compraFecha;
    private BigDecimal compraTotal;
    private String compraObservaciones;
    private CompraEstado estado;
    private LocalDateTime auditoriaFechaCreacion;
    private List<CompraDetalleResponseDTO> detalles;

    public static CompraResponseDTO fromEntity (farmacias.AppOchoa.model.Compra compra){
        return CompraResponseDTO.builder()
                .compraId(compra.getCompraId())
                .sucursal(compra.getSucursal() != null?
                        SucursalSimpleDTO.fromEntity(compra.getSucursal()) : null)
                .usuario(compra.getUsuario() != null?
                        UsuarioSimpleDTO.fromEntity(compra.getUsuario()) : null)
                .compraFecha(compra.getCompraFecha())
                .compraTotal(compra.getCompraTotal())
                .compraObservaciones(compra.getCompraObservaciones())
                .estado(compra.getCompraEstado())
                .auditoriaFechaCreacion(compra.getAuditoriaFechaCreacion())
                .build();




    }
}
