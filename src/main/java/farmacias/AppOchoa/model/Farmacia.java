package farmacias.AppOchoa.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "farmacias")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Farmacia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long farmaciaId;

    @Column(name = "farmacia_nombre", nullable = false)
    private String farmaciaNombre;

    @Column(name = "farmacia_nit", nullable = false, unique = true)
    private String farmaciaNit;

    @Column(name = "farmacia_email", nullable = false, unique = true)
    private String farmaciaEmail;

    @Column(name = "farmacia_telefono")
    private String farmaciaTelefono;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false)
    @Builder.Default
    private PlanTipo planTipo = PlanTipo.basico;

    @Column(name = "farmacia_activa", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean farmaciaActiva = true;

    @Column(name = "en_periodo_prueba", columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean enPeriodoPrueba = true;

    @Column(name = "prueba_hasta")
    private LocalDate pruebaHasta;

    @Column(name = "suscripcion_vigencia")
    private LocalDate suscripcionVigencia;

    @Column(name = "max_sucursales", nullable = false)
    private Integer maxSucursales;

    @Column(name = "max_usuarios", nullable = false)
    private Integer maxUsuarios;

    @Column(name = "auditoria_fecha_creacion", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime auditoriaFechaCreacion;
}