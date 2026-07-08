package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.Caja;
import farmacias.AppOchoa.model.Sucursal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {

    boolean existsBySucursalNombre(String nombre);
    List<Sucursal> findBySucursalEstadoTrue();
    Page<Sucursal> findBySucursalEstadoTrue(Pageable pageable);
    List<Sucursal> findBySucursalNombreContainingIgnoreCase(String nombre);
    Page<Sucursal> findBySucursalNombreContainingIgnoreCase(String nombre, Pageable pageable);
    Page<Sucursal> findByFarmacia_FarmaciaIdAndSucursalEstadoTrue(Long farmaciaId, Pageable pageable);
    boolean existsByFarmacia_FarmaciaIdAndSucursalNombre(Long farmaciaId, String nombre);
    Page<Sucursal> findByFarmacia_FarmaciaId(Long farmaciaId, Pageable pageable);
    long countByFarmacia_FarmaciaIdAndSucursalEstadoTrue(Long farmaciaId);
    Optional<Sucursal> findBySucursalIdAndFarmacia_FarmaciaId(Long sucursalId, Long farmaciaId);
    Optional<Sucursal> findByFarmacia_FarmaciaId(Long farmaciaId);

    @Query("SELECT s FROM Sucursal s WHERE s.farmacia.farmaciaId = :farmaciaId AND (" +
            "LOWER(s.sucursalNombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(s.sucursalDireccion) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(s.sucursalTelefono) LIKE LOWER(CONCAT('%', :texto, '%')))")
    Page<Sucursal> buscarPorTexto(@Param("farmaciaId") Long farmaciaId, @Param("texto") String texto, Pageable pageable);
}