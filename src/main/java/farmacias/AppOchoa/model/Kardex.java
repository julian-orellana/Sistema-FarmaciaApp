package farmacias.AppOchoa.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "kardex")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Kardex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kardex_id")
    private Long kardexId;

    @Column(name = "fecha_ingreso", nullable = false,updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaIngreso;

    @Column(name = "precio_compra")
    private BigDecimal precioCompra;

    @Column(name = "precio_venta")
    private BigDecimal precioVenta;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "ventas")
    private BigDecimal ventas;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false)
    private KardexEstado kardexEstado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmacia_id", nullable = false)
    private Farmacia farmacia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

}
