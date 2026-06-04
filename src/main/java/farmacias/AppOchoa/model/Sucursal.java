package farmacias.AppOchoa.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sucursales")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Sucursal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sucursal_id")
    private Long sucursalId;

    @Column(name = "sucursal_nombre", nullable = false, length = 100)
    private String sucursalNombre;

    @Column(name = "sucursal_direccion", nullable = false, length = 200)
    private String sucursalDireccion;

    @Column(name = "sucursal_telefono", length = 20)
    private String sucursalTelefono;

    @Column(name = "sucursal_estado", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean sucursalEstado = true;

    @Column(name = "auditoria_fecha_creacion", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime auditoriaFechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmacia_id", nullable = false)
    private Farmacia farmacia;
}


