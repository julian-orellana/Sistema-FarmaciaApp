package farmacias.AppOchoa.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "caja_cortes")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CajaCorte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long corteId;

    @Column(name = "corte_total_efectivo", nullable = false)
    private BigDecimal corteTotalEfectivo;

    @Column(name = "corte_total_tarjeta_credito", nullable = false)
    private  BigDecimal corteTotalTarjetaCredito;

    @Column(name = "corte_total_tarjeta_debito", nullable = false)
    private BigDecimal corteTotalTarjetaDebito;

    @Column(name = "corte_total_ventas", nullable = false)
    private BigDecimal corteTotalVentas;

    @CreationTimestamp
    @Column(name = "corte_fecha", nullable = false, updatable = false)
    private LocalDateTime corteFecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id")
    private CajaSesiones cajaSesiones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_supervisor_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmacia_id")
    private Farmacia farmacia;


}