package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.dto.producto.ProductoSimpleDTO;
import farmacias.AppOchoa.model.Caja;
import farmacias.AppOchoa.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    boolean existsByProductoNombre(String productoNombre);
    boolean existsByProductoCodigoBarras(String productoCodigoBarras);
    Optional<Producto> findByProductoCodigoBarras(String productoCodigoBarras);
    Page<Producto> findByProductoEstadoTrue(Pageable pageable);

    boolean existsByFarmacia_FarmaciaIdAndProductoNombre(Long farmaciaId, String productoNombre);
    boolean existsByFarmacia_FarmaciaIdAndProductoCodigoBarras(Long farmaciaId, String codigoBarras);
    Optional<Producto> findByFarmacia_FarmaciaIdAndProductoCodigoBarras(Long farmaciaId, String codigoBarras);
    Page<Producto> findByFarmacia_FarmaciaIdAndProductoEstadoTrue(Long farmaciaId, Pageable pageable);
    Page<Producto> findByFarmacia_FarmaciaId(Long farmaciaId, Pageable pageable);
    @Query("SELECT p FROM Producto p WHERE " +
            "LOWER(p.productoNombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(p.productoCodigoBarras) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Producto> buscarPorTexto(@Param("texto") String texto, Pageable pageable);
}