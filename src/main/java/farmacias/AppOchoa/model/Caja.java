package farmacias.AppOchoa.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cajas")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Caja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cajaId;

    @Column(name = "caja_nombre", nullable = false, length = 100)
    private String cajaNombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "caja_estado", nullable = false)
    @Builder.Default
    private CajaEstado cajaEstado = CajaEstado.activa;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmacia_id")
    private Farmacia farmacia;

}
