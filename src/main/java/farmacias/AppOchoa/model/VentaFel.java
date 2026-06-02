package farmacias.AppOchoa.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "ventas_fel")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VentaFel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fel_id")
    private Long felId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @Enumerated(EnumType.STRING)
    @Column(name = "fel_estado", nullable = false)
    @Builder.Default
    private VentaFelEstado felEstado = VentaFelEstado.Pendiente;

    @Column(name = "fel_uuid", length = 36)
    private String felUuid;

    @Column(name = "fel_numero_autorizacion", length = 50)
    private String felNumeroAutorizacion;

    @Column(name = "fel_fecha_certificacion")
    private LocalDateTime felFechaCertificacion;

    @Column(name = "fel_xml_dte", columnDefinition = "TEXT")
    private String felXmlDte;

    @Column(name = "fel_error_descripcion", columnDefinition = "TEXT")
    private String felErrorDescripcion;

    @Column(name = "fel_intentos", nullable = false)
    @Builder.Default
    private Integer felIntentos = 0;

    @Column(name = "auditoria_fecha_creacion", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime auditoriaFechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmacia_id")
    private Farmacia farmacia;

}