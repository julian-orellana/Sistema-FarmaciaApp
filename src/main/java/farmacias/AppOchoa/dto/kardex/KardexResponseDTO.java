package farmacias.AppOchoa.dto.kardex;

import farmacias.AppOchoa.dto.farmacia.FarmaciaSimpleDTO;
import farmacias.AppOchoa.dto.producto.ProductoSimpleDTO;
import farmacias.AppOchoa.dto.usuario.UsuarioSimpleDTO;
import farmacias.AppOchoa.model.Kardex;
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
public class KardexResponseDTO {
    private Long kardexId;
    private ProductoSimpleDTO producto;
    private FarmaciaSimpleDTO farmacia;
    private UsuarioSimpleDTO usuario;
    private LocalDateTime fechaMovimiento;
    private Integer cantidad;
    private Integer stockAnterior;
    private Integer stockResultante;
    private BigDecimal costoUnitario;
    private Long referenciaId;
    private String referenciaTipo;
    private String observacion;

    public static KardexResponseDTO fromEntity (Kardex kardex){
        return KardexResponseDTO.builder()
                .kardexId(kardex.getKardexId())
                .fechaMovimiento(kardex.getFechaMovimiento())
                .cantidad(kardex.getCantidad())
                .stockAnterior(kardex.getStockAnterior())
                .stockResultante(kardex.getStockResultante())
                .costoUnitario(kardex.getCostoUnitario())
                .referenciaTipo(kardex.getReferenciaTipo())
                .observacion(kardex.getObservacion())
                .producto(kardex.getProducto() != null ?
                        ProductoSimpleDTO.fromEntity(kardex.getProducto()): null)
                .farmacia(kardex.getFarmacia() != null ?
                        FarmaciaSimpleDTO.fromEntity(kardex.getFarmacia()): null)
                .usuario(kardex.getUsuario() != null ?
                        UsuarioSimpleDTO.fromEntity(kardex.getUsuario()):  null)
                .referenciaId(kardex.getReferenciaId())
                .build();


    }



}
