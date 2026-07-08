package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.Kardex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KardexRepository extends JpaRepository<Kardex, Long> {
    @Query("SELECT k FROM Kardex k " +
            "JOIN FETCH k.producto " +
            "JOIN FETCH k.farmacia " +
            "JOIN FETCH k.usuario " +
            "WHERE k.producto.productoId = :productoId AND k.farmacia.farmaciaId = :farmaciaId " +
            "ORDER BY k.fechaMovimiento DESC")
    List<Kardex> findByProductoAndFarmacia(@Param("productoId") Long productoId,
                                           @Param("farmaciaId") Long farmaciaId);
}
