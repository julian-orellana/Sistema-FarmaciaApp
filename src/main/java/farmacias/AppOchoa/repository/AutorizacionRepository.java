package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.Autorizacion;
import farmacias.AppOchoa.model.AutorizacionTipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AutorizacionRepository extends JpaRepository<Autorizacion, Long> {
    List<Autorizacion> findBySupervisorUsuarioId(Long supervisorId);
    List<Autorizacion> findByAutorizacionFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Autorizacion> findByAutorizacionTipo(AutorizacionTipo autorizacionTipo);
    @Query("SELECT a FROM Autorizacion a WHERE " +
            "a.cajero.farmacia.farmaciaId = :farmaciaId AND (" +
            "LOWER(a.cajero.nombreUsuarioUsuario) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(a.supervisor.nombreUsuarioUsuario) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(CAST(a.autorizacionTipo AS string)) LIKE LOWER(CONCAT('%', :texto, '%')))")
    Page<Autorizacion> buscarPorTexto(@Param("farmaciaId") Long farmaciaId, @Param("texto") String texto, Pageable pageable);
    Page<Autorizacion> findByFarmacia_FarmaciaId(Long farmaciaId, Pageable pageable);

}
