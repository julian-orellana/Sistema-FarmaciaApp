package farmacias.AppOchoa.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "autorizaciones")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Autorizacion{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long autorizacionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "autorizacion_tipo", nullable = false)
    private AutorizacionTipo autorizacionTipo;

    @CreationTimestamp
    @Column(name = "autorizacion_fecha", nullable = false, updatable = false)
    private LocalDateTime autorizacionFecha;

    @Column(name = "autorizacion_referencia_id", nullable = false)
    private Long autorizacionReferenciaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_cajero_id")
    private Usuario cajero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_supervisor_id")
    private Usuario supervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmacia_id")
    private Farmacia farmacia;



}
