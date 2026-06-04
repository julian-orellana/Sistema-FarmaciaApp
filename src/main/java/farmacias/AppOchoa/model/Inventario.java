package farmacias.AppOchoa.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventario")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventario_id")
    private Long inventarioId;

    @Column(name = "inventario_cantidad_actual", nullable = false)
    private Integer inventarioCantidadActual;

    @Column(name = "inventario_cantidad_minima", nullable = false)
    private Integer inventarioCantidadMinima;

    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmacia_id")
    private Farmacia farmacia;


}
