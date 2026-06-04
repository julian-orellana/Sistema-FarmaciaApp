package farmacias.AppOchoa.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "suscripcion_pagos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SuscripcionPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pagoId;

    @Column(name = "pago_monto")
    private BigDecimal pagoMonto;

    @Enumerated(EnumType.STRING)
    @Column(name = "pago_plan", nullable = false)
    @Builder.Default
    private PagoPlan pagoPlan = PagoPlan.basico;

    @Enumerated(EnumType.STRING)
    @Column(name = "pago_metodo", nullable = false)
    @Builder.Default
    private PagoMetodo pagoMetodo = PagoMetodo.efectivo;

    @Column(name = "pago_referencia")
    private String pagoReferencia;

    @Column(name = "pago_periodo_inicio", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDate pagoPeriodoInicio;

    @Column(name = "pago_periodo_fin", nullable = false, updatable = false)
    private LocalDate pagoPeriodoFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "pago_estado", nullable = false)
    @Builder.Default
    private PagoEstado pagoEstado = PagoEstado.pendiente;

    @Column(name = "pago_notas", columnDefinition = "TEXT")
    private String pagoNotas;

    @Column(name = "auditoria_fecha_creacion", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime auditoriaFechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmacia_id")
    private Farmacia farmacia;

}
