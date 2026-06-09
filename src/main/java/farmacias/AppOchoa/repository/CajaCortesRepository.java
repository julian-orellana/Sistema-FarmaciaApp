package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.CajaCorte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface CajaCortesRepository extends JpaRepository<CajaCorte, Long> {

    // Buscar cortes de una sesión específica
    List<CajaCorte> findByCajaSesionesSesionId(Long sesionId);

    // Buscar cortes autorizados por un supervisor
    List<CajaCorte> findByUsuarioUsuarioId(Long usuarioId);

    // Buscar cortes por rango de fechas
    List<CajaCorte> findByCorteFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    @Query("SELECT c FROM CajaCorte c WHERE " +
            "c.cajaSesiones.farmacia.farmaciaId = :farmaciaId AND (" +
            "LOWER(c.usuario.nombreUsuarioUsuario) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(c.usuario.usuarioNombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(c.usuario.usuarioApellido) LIKE LOWER(CONCAT('%', :texto, '%')))")
    Page<CajaCorte> buscarPorTexto(@Param("farmaciaId") Long farmaciaId, @Param("texto") String texto, Pageable pageable);
    Page<CajaCorte> findByFarmacia_FarmaciaId(Long farmaciaId, Pageable pageable);
    java.util.Optional<CajaCorte> findByCorteIdAndFarmacia_FarmaciaId(Long corteId, Long farmaciaId);
}