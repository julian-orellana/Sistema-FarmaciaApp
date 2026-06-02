package farmacias.AppOchoa.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compras")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compra_id")
    private Long compraId;

    @Column(name = "compra_fecha", nullable = false)
    private LocalDate compraFecha;

    @Column(name = "compra_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal compraTotal;


    @Column(name = "compra_observaciones", columnDefinition = "TEXT")
    private String compraObservaciones;

    @Enumerated(EnumType.STRING)
    @Column(name = "compra_estado", nullable = false, length = 20)
    @Builder.Default
    private CompraEstado compraEstado = CompraEstado.activa;

    @Column(name = "auditoria_fecha_creacion", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime auditoriaFechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CompraDetalle> detalles = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmacia_id")
    private Farmacia farmacia;
}