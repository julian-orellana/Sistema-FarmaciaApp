package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.NotaEstado;
import farmacias.AppOchoa.model.VentaFelNotasCredito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VentaFelNotasCreditoRepository extends JpaRepository<VentaFelNotasCredito, Long> {

    List<VentaFelNotasCredito> findByNotaEstado(NotaEstado notaEstado);
    List<VentaFelNotasCredito> findByAuditoriaFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
    Optional<VentaFelNotasCredito> findByNotaUuid(String uuid);
    List<VentaFelNotasCredito> findByNotaNumeroAutorizacion(String nota);

    List<VentaFelNotasCredito> findByVentaFel_FelId(Long felId);
    Page<VentaFelNotasCredito> findByNotaEstado(NotaEstado notaEstado, Pageable pageable);
    Page<VentaFelNotasCredito> findByAuditoriaFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);
    Page<VentaFelNotasCredito> findByNotaEstadoAndAuditoriaFechaCreacionBetween(NotaEstado notaEstado, LocalDateTime inicio, LocalDateTime fin, Pageable pageable);
    @Query("SELECT n FROM VentaFelNotasCredito n WHERE " +
            "LOWER(n.notaNumeroAutorizacion) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(n.notaMotivo) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(n.notaUuid) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(CAST(n.notaEstado AS string)) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<VentaFelNotasCredito> buscarPorTexto(@Param("texto") String texto, Pageable pageable);

    @Query("SELECT n FROM VentaFelNotasCredito n WHERE n.sucursal.sucursalId = :sucursalId AND (" +
            "LOWER(n.notaNumeroAutorizacion) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(n.notaMotivo) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(n.notaUuid) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(CAST(n.notaEstado AS string)) LIKE LOWER(CONCAT('%', :texto, '%')))")
    Page<VentaFelNotasCredito> buscarPorTexto(@Param("sucursalId") Long sucursalId, @Param("texto") String texto, Pageable pageable);
    Page<VentaFelNotasCredito> findByFarmacia_FarmaciaId(Long farmaciaId, Pageable pageable);
    java.util.Optional<VentaFelNotasCredito> findByNotaIdAndFarmacia_FarmaciaId(Long notaId, Long farmaciaId);
    Page<VentaFelNotasCredito> findByFarmacia_FarmaciaIdAndNotaEstadoAndAuditoriaFechaCreacionBetween(Long farmaciaId, NotaEstado notaEstado, LocalDateTime inicio, LocalDateTime fin, Pageable pageable);
    Page<VentaFelNotasCredito> findByFarmacia_FarmaciaIdAndNotaEstado(Long farmaciaId, NotaEstado notaEstado, Pageable pageable);
    Page<VentaFelNotasCredito> findByFarmacia_FarmaciaIdAndAuditoriaFechaCreacionBetween(Long farmaciaId, LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

    // Métodos por sucursal (eje de tenancy operativo)
    Page<VentaFelNotasCredito> findBySucursal_SucursalId(Long sucursalId, Pageable pageable);
    java.util.Optional<VentaFelNotasCredito> findByNotaIdAndSucursal_SucursalId(Long notaId, Long sucursalId);
    Page<VentaFelNotasCredito> findBySucursal_SucursalIdAndNotaEstadoAndAuditoriaFechaCreacionBetween(Long sucursalId, NotaEstado notaEstado, LocalDateTime inicio, LocalDateTime fin, Pageable pageable);
    Page<VentaFelNotasCredito> findBySucursal_SucursalIdAndNotaEstado(Long sucursalId, NotaEstado notaEstado, Pageable pageable);
    Page<VentaFelNotasCredito> findBySucursal_SucursalIdAndAuditoriaFechaCreacionBetween(Long sucursalId, LocalDateTime inicio, LocalDateTime fin, Pageable pageable);
}