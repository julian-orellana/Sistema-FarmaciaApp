package farmacias.AppOchoa.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id")
    private Long productoId;

    @Column(name = "producto_nombre", nullable = false, length = 150)
    private String productoNombre;

    @Column(name = "producto_codigo_barras", unique = true, length = 50)
    private String productoCodigoBarras;

    @Column(name = "producto_precio_compra", nullable = false, precision = 12, scale = 2)
    private BigDecimal productoPrecioCompra;

    @Column(name = "producto_precio_venta", nullable = false, precision = 12, scale = 2)
    private BigDecimal productoPrecioVenta;

    // Nuevo campo de IVA sincronizado con el SQL DECIMAL(5,2)
    @Column(name = "producto_iva", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal productoIva = BigDecimal.ZERO;

    @Column(name = "producto_requiere_receta", nullable = false)
    @Builder.Default
    private Boolean productoRequiereReceta = false;

    @Column(name = "producto_estado", nullable = false)
    @Builder.Default
    private Boolean productoEstado = true;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presentacion_id")
    private Presentacion presentacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmacia_id", nullable = false)
    private Farmacia farmacia;
}