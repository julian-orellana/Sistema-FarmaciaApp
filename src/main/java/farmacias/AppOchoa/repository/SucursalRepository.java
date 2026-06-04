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

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {

    // Validar nombre único
    boolean existsBySucursalNombre(String nombre);
    // Listar solo sucursales activas (sin paginación)
    List<Sucursal> findBySucursalEstadoTrue();
    // Listar solo sucursales activas CON paginación
    Page<Sucursal> findBySucursalEstadoTrue(Pageable pageable);
    // Buscar por nombre (sin paginación)
    List<Sucursal> findBySucursalNombreContainingIgnoreCase(String nombre);
    // Buscar por nombre CON paginación
    Page<Sucursal> findBySucursalNombreContainingIgnoreCase(String nombre, Pageable pageable);
    Page<Sucursal> findByFarmacia_FarmaciaIdAndSucursalEstadoTrue(Long farmaciaId, Pageable pageable);
    boolean existsByFarmacia_FarmaciaIdAndSucursalNombre(Long farmaciaId, String nombre);
    Page<Sucursal> findByFarmacia_FarmaciaId(Long farmaciaId, Pageable pageable);
    @Query("SELECT s FROM Sucursal s WHERE " +
            "LOWER(s.sucursalNombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(s.sucursalDireccion) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(s.sucursalTelefono) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Sucursal> buscarPorTexto(@Param("texto") String texto, Pageable pageable);
}