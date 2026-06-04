package farmacias.AppOchoa.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "venta_pagos")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class VentaPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pagoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_de_pago", nullable = false)
    private MetodoPagoEstado metodoPago;

    @Column(name = "monto_recibido", nullable = false)
    private BigDecimal montoRecibido;

    @Column(name = "monto_vuelto", nullable = false)
    @Builder.Default
    private BigDecimal montoVuelto = BigDecimal.ZERO;

    @Column(name = "referencia_transaccion",length = 100)
    private String referenciaTransaccion;

    @CreationTimestamp
    @Column(name = "auditoria_fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime auditoriaFechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caja_sesion_id")
    private CajaSesiones cajaSesiones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmacia_id")
    private Farmacia farmacia;
}
