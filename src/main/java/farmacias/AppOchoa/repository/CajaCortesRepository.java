package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.CajaCorte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CajaCortesRepository extends JpaRepository<CajaCorte, Long> {

    List<CajaCorte> findByCajaSesionesSesionId(Long sesionId);

    List<CajaCorte> findByUsuarioUsuarioId(Long usuarioId);

    List<CajaCorte> findByCorteFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT c FROM CajaCorte c WHERE " +
            "c.sucursal.sucursalId = :sucursalId AND (" +
            "LOWER(c.usuario.nombreUsuarioUsuario) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(c.usuario.usuarioNombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(c.usuario.usuarioApellido) LIKE LOWER(CONCAT('%', :texto, '%')))")
    Page<CajaCorte> buscarPorTexto(@Param("sucursalId") Long sucursalId, @Param("texto") String texto, Pageable pageable);

    Page<CajaCorte> findByFarmacia_FarmaciaId(Long farmaciaId, Pageable pageable);

    Optional<CajaCorte> findByCorteIdAndFarmacia_FarmaciaId(Long corteId, Long farmaciaId);

    // Métodos por sucursal (eje de tenancy operativo)
    Page<CajaCorte> findBySucursal_SucursalId(Long sucursalId, Pageable pageable);

    Optional<CajaCorte> findByCorteIdAndSucursal_SucursalId(Long corteId, Long sucursalId);
}