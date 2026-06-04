package farmacias.AppOchoa.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "presentaciones")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Presentacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "presentacion_id")
    private Long presentacionId;

    @Column(name = "presentacion_nombre", nullable = false, length = 50)
    private String presentacionNombre;

    @Column(name = "presentacion_estado", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean presentacionEstado = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmacia_id")
    private Farmacia farmacia;




}
