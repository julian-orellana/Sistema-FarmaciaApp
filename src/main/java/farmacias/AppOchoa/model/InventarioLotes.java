package farmacias.AppOchoa.model;

import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "inventario_lotes")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class InventarioLotes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lote_id")
    private Long loteId;

    @Column(name = "lote_numero", nullable = false, length = 50)
    private String loteNumero;

    @Column(name = "lote_fecha_vencimiento", nullable = false)
    private LocalDate loteFechaVencimiento;

    @Column(name = "lote_cantidad_inicial", nullable = false)
    private Integer loteCantidadInicial;

    @Column(name = "lote_cantidad_actual", nullable = false)
    private Integer loteCantidadActual;

    @Column(name = "lote_precio_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal lotePrecioCompra;

    @Enumerated(EnumType.STRING)
    @Column(name = "lote_estado", nullable = false)
    private LoteEstado loteEstado = LoteEstado.disponible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmacia_id")
    private Farmacia farmacia;


}
