package farmacias.AppOchoa.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "caja_sesiones")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CajaSesiones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sesionId;

    @CreationTimestamp
    @Column(name = "sesion_fecha_apertura", nullable = false, updatable = false )
    private LocalDateTime sesionFechaApertura;

    @Column(name = "sesion_fecha_cierre")
    private LocalDateTime sesionFechaCierre;

    @Column(name = "sesion_fondo_inicial", nullable = false)
    private BigDecimal sesionFondoInicial;

    @Enumerated(EnumType.STRING)
    @Column(name = "sesion_estado", nullable = false)
    @Builder.Default
    private SesionEstado sesionEstado = SesionEstado.abierta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caja_id")
    private  Caja caja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmacia_id")
    private Farmacia farmacia;


}
