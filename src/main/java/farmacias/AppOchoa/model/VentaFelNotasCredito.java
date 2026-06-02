package farmacias.AppOchoa.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ventas_fel_notas_credito")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VentaFelNotasCredito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notaId;

    @Column(name = "nota_uuid", length = 36)
    private String notaUuid;

    @Column(name = "nota_numero_autorizacion", nullable = false, length = 50)
    private String notaNumeroAutorizacion;

    @Column(name = "nota_motivo", columnDefinition = "TEXT", nullable = false)
    private String notaMotivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "nota_estado", nullable = false)
    @Builder.Default
    private NotaEstado notaEstado = NotaEstado.pendiente;

    @Column(name = "nota_xml", columnDefinition = "TEXT")
    private String notaXml;

    @CreationTimestamp
    @Column(name = "auditoria_fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime auditoriaFechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fel_id")
    private VentaFel ventaFel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmacia_id")
    private Farmacia farmacia;

}
