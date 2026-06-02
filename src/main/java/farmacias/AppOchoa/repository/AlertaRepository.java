package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.Alerta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    // NOTA: Usamos Producto_ProductoId porque en la entidad Producto el campo es productoId
    Page<Alerta> findByProducto_ProductoId(Long productoId, Pageable pageable);
    Page<Alerta> findByAlertaLeidaFalse(Pageable pageable);
    Page<Alerta> findByFarmacia_FarmaciaId(Long farmaciaId, Pageable pageable);
    @Query("SELECT a FROM Alerta a WHERE " +
            "LOWER(a.alertaMensaje) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Alerta> buscarPorTexto(@Param("texto") String texto, Pageable pageable);
}