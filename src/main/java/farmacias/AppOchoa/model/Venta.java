package farmacias.AppOchoa.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venta_id")
    private Long ventaId;

    @Column(name = "venta_uuid", length = 50)
    private String ventaUuid;

    @Column(name = "venta_numero_autorizacion", length = 50)
    private String ventaNumeroAutorizacion;

    @Column(name = "venta_fecha_certificacion")
    private LocalDateTime ventaFechaCertificacion;

    @Column(name = "venta_numero_factura", unique = true, length = 50)
    private String ventaNumeroFactura;

    @Column(name = "venta_serie", length = 10)
    @Builder.Default
    private String ventaSerie = "A";

    //DATOS DEL CLIENTE
    @Column(name = "venta_nit_cliente", length = 20)
    @Builder.Default
    private String ventaNitCliente = "CF";

    @Column(name = "venta_nombre_cliente", length = 150)
    @Builder.Default
    private String ventaNombreCliente = "Consumidor Final";

    //DATOS DE LA OPERACIÓN
    @Column(name = "venta_fecha", nullable = false, updatable = false)
    private LocalDateTime ventaFecha;

    @PrePersist
    protected void onCreate() {
        if (ventaFecha == null) {
            ventaFecha = LocalDateTime.now();
        }
    }

    @Column(name = "venta_subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal ventaSubtotal;

    @Column(name = "venta_descuento", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal ventaDescuento = BigDecimal.ZERO;

    @Column(name = "venta_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal ventaTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "venta_estado", nullable = false)
    @Builder.Default
    private VentaEstado ventaEstado = VentaEstado.completada;

    @Column(name = "auditoria_fecha_creacion", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime auditoriaFechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VentaDetalle> detalles = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmacia_id")
    private Farmacia farmacia;

}