package farmacias.AppOchoa.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fel_credenciales")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CredencialesFel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long credencialId;

    @Column(name = "ambiente", nullable = false)
    private String ambiente;

    @Column(name = "certificador", nullable = false)
    private String certificador;

    @Column(name = "credencial_usuario_cifrado", columnDefinition = "TEXT")
    private String credencialUsuarioCifrado;

    @Column(name = "credencial_secreto_cifrado", columnDefinition = "TEXT")
    private String credencialSecretoCifrado;

    @Column(name = "credencial_extra_cifrado", columnDefinition = "TEXT")
    private String credencialExtraCifrado;

    @Column(name = "credencial_cliente", nullable = false)
    private Long credencialCliente;

    @Column(name = "credencial_contrato", nullable = false)
    private Long credencialContrato;

    @Column(name = "firmar_emisor", nullable = false)
    private Boolean firmarEmisor;

    @Column(name = "validar_identificador", nullable = false)
    @Builder.Default
    private Boolean validarIdentificador = true;

    @Column(name = "activa", nullable = false)
    @Builder.Default
    private Boolean activa = false;

    @Column(name = "fecha_validacion")
    private LocalDateTime fechaValidacion;

    @CreationTimestamp
    @Column(name = "auditoria_fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime auditoriaFechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

}
