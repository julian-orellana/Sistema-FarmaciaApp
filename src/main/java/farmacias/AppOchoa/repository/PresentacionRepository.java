package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.Presentacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresentacionRepository extends JpaRepository<Presentacion, Long> {

    // Para validar si existe antes de crear
    boolean existsByPresentacionNombre(String nombre);
    // Para listar presentaciones activas (sin paginación)
    List<Presentacion> findByPresentacionEstadoTrue();
    // Para listar presentaciones activas CON paginación
    Page<Presentacion> findByPresentacionEstadoTrue(Pageable pageable);
    Page<Presentacion> findByFarmacia_FarmaciaIdAndPresentacionEstadoTrue(Long farmaciaId, Pageable pageable);
    boolean existsByFarmacia_FarmaciaIdAndPresentacionNombre(Long farmaciaId, String nombre);
    Page<Presentacion> findByFarmacia_FarmaciaId(Long farmaciaId, Pageable pageable);
    java.util.Optional<Presentacion> findByPresentacionIdAndFarmacia_FarmaciaId(Long presentacionId, Long farmaciaId);

    // Métodos por sucursal (eje de tenancy operativo)
    Page<Presentacion> findBySucursal_SucursalIdAndPresentacionEstadoTrue(Long sucursalId, Pageable pageable);
    boolean existsBySucursal_SucursalIdAndPresentacionNombre(Long sucursalId, String nombre);
    Page<Presentacion> findBySucursal_SucursalId(Long sucursalId, Pageable pageable);
    java.util.Optional<Presentacion> findByPresentacionIdAndSucursal_SucursalId(Long presentacionId, Long sucursalId);

    @Query("SELECT p FROM Presentacion p WHERE " +
            "LOWER(p.presentacionNombre) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Presentacion> buscarPorTexto(@Param("texto") String texto, Pageable pageable);

    @Query("SELECT p FROM Presentacion p WHERE p.sucursal.sucursalId = :sucursalId AND " +
            "LOWER(p.presentacionNombre) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Presentacion> buscarPorTexto(@Param("sucursalId") Long sucursalId, @Param("texto") String texto, Pageable pageable);
}