package farmacias.AppOchoa.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Alerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alerta_id", nullable = false)
    private Long alertaId;


    @Enumerated(EnumType.STRING)
    @Column(name = "alerta_tipo", nullable = false)
    private AlertaTipo alertaTipo = AlertaTipo.stock_bajo;


    @Column(name = "alerta_mensaje", columnDefinition = "TEXT")
    private String alertaMensaje;

    @Column(name = "alerta_fecha",nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime alertaFecha;

    @Column(name = "alerta_leida")
    private Boolean alertaLeida = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private InventarioLotes lote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmacia_id", nullable = false)
    private Farmacia farmacia;

}

