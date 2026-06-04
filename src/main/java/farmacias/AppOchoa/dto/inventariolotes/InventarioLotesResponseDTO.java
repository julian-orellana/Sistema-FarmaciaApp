package farmacias.AppOchoa.dto.inventariolotes;

import farmacias.AppOchoa.dto.producto.ProductoSimpleDTO;
import farmacias.AppOchoa.dto.sucursal.SucursalSimpleDTO;
import farmacias.AppOchoa.model.InventarioLotes;
import farmacias.AppOchoa.model.LoteEstado;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventarioLotesResponseDTO {

    private Long loteId;

    private ProductoSimpleDTO producto;

    private SucursalSimpleDTO sucursal;

    private String numeroLote;

    private LocalDate fechaVencimiento;

    private Integer cantidadInicial;

    private Integer cantidadActual;

    private BigDecimal precioCompra;

    private LoteEstado estado;

    // Campo calculado útil
    private Integer diasParaVencer;  // Cuántos días faltan para vencer

    public static InventarioLotesResponseDTO fromEntity(InventarioLotes lote) {
        // Calcular días para vencer
        long dias = java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.now(),
                lote.getLoteFechaVencimiento()
        );

        return InventarioLotesResponseDTO.builder()
                .loteId(lote.getLoteId())
                .producto(lote.getProducto() != null ?
                        ProductoSimpleDTO.fromEntity(lote.getProducto()) : null)
                .sucursal(lote.getSucursal() != null ?
                        SucursalSimpleDTO.fromEntity(lote.getSucursal()) : null)
                .numeroLote(lote.getLoteNumero())
                .fechaVencimiento(lote.getLoteFechaVencimiento())
                .cantidadInicial(lote.getLoteCantidadInicial())
                .cantidadActual(lote.getLoteCantidadActual())
                .precioCompra(lote.getLotePrecioCompra())
                .estado(lote.getLoteEstado())
                .diasParaVencer((int) dias)
                .build();
    }
}