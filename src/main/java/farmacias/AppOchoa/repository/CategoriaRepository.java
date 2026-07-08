package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.Caja;
import farmacias.AppOchoa.model.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByCategoriaEstadoTrue();
    // Este método debe recibir Pageable para que el Service no marque error
    Page<Categoria> findByCategoriaEstadoTrue(Pageable pageable);
    boolean existsByCategoriaNombre(String nombre);
    Page<Categoria> findByFarmacia_FarmaciaIdAndCategoriaEstadoTrue(Long farmaciaId, Pageable pageable);
    boolean existsByFarmacia_FarmaciaIdAndCategoriaNombre(Long farmaciaId, String nombre);
    boolean existsBySucursal_SucursalIdAndCategoriaNombre(Long sucursalId, String categoriaNombre);
    Page<Categoria> findByFarmacia_FarmaciaId(Long farmaciaId, Pageable pageable);
    java.util.Optional<Categoria> findByCategoriaIdAndFarmacia_FarmaciaId(Long categoriaId, Long farmaciaId);
    @Query("SELECT c FROM Categoria c WHERE " +
            "LOWER(c.categoriaNombre) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Categoria> buscarPorTexto(@Param("texto") String texto, Pageable pageable);

    @Query("SELECT c FROM Categoria c WHERE c.sucursal.sucursalId = :sucursalId AND " +
            "LOWER(c.categoriaNombre) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Categoria> buscarPorTexto(@Param("sucursalId") Long sucursalId, @Param("texto") String texto, Pageable pageable);

    Optional<Categoria> findByCategoriaIdAndSucursal_SucursalId(Long categoriaId, Long sucursalId);
    Page<Categoria> findBySucursal_SucursalId(Long sucursalId, Pageable pageable);
}