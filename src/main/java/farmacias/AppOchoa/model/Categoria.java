package farmacias.AppOchoa.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categorias")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoria_id")
    private Long categoriaId;

    @Column(name = "categoria_nombre", nullable = false, length = 100)
    private String categoriaNombre;

    @Column(name = "categoria_estado", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean categoriaEstado = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmacia_id")
    private Farmacia farmacia;
}
