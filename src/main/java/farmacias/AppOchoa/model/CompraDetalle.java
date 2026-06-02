package farmacias.AppOchoa.model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "compra_detalle")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CompraDetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detalle_id", nullable = false)
    private Long detalleId;

    @Column(name = "detalle_cantidad", nullable = false)
    private Integer detalleCantidad;

    @Column(name = "detalle_precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal detallePrecioUnitario;

    @Column(name = "detalle_subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal detalleSubtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_id")
    private Compra compra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private InventarioLotes loteId;

}
