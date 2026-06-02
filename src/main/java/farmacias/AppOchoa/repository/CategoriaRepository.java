package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.Caja;
import farmacias.AppOchoa.model.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByCategoriaEstadoTrue();
    // Este método debe recibir Pageable para que el Service no marque error
    Page<Categoria> findByCategoriaEstadoTrue(Pageable pageable);
    boolean existsByCategoriaNombre(String nombre);
    Page<Categoria> findByFarmacia_FarmaciaIdAndCategoriaEstadoTrue(Long farmaciaId, Pageable pageable);
    boolean existsByFarmacia_FarmaciaIdAndCategoriaNombre(Long farmaciaId, String nombre);
    Page<Categoria> findByFarmacia_FarmaciaId(Long farmaciaId, Pageable pageable);
    @Query("SELECT c FROM Categoria c WHERE " +
            "LOWER(c.categoriaNombre) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Categoria> buscarPorTexto(@Param("texto") String texto, Pageable pageable);
}