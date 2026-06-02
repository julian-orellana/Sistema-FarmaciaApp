package farmacias.AppOchoa.dto.inventariolotes;

import farmacias.AppOchoa.model.InventarioLotes;
import farmacias.AppOchoa.model.LoteEstado;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventarioLotesSimpleDTO {

    private Long loteId;

    private String numeroLote;

    private LocalDate fechaVencimiento;

    private Integer cantidadActual;

    private LoteEstado estado;

    public static InventarioLotesSimpleDTO fromEntity(InventarioLotes lote) {
        return InventarioLotesSimpleDTO.builder()
                .loteId(lote.getLoteId())
                .numeroLote(lote.getLoteNumero())
                .fechaVencimiento(lote.getLoteFechaVencimiento())
                .cantidadActual(lote.getLoteCantidadActual())
                .estado(lote.getLoteEstado())
                .build();
    }
}