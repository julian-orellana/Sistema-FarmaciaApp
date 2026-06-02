package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.VentaFel;
import farmacias.AppOchoa.model.VentaFelEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaFelRepository extends JpaRepository<VentaFel, Long> {

    List<VentaFel> findByFelEstado(VentaFelEstado ventaFelEstado);
    List<VentaFel> findByAuditoriaFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
    List<VentaFel> findByFelEstadoAndFelIntentosLessThan(VentaFelEstado estado, Integer maxIntentos);
    @Query("SELECT f FROM VentaFel f WHERE " +
            "LOWER(f.felUuid) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(f.felNumeroAutorizacion) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(f.venta.ventaNitCliente) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(f.venta.ventaNombreCliente) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(CAST(f.felEstado AS string)) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<VentaFel> buscarPorTexto(@Param("texto") String texto, Pageable pageable);
    Page<VentaFel> findByFarmacia_FarmaciaId(Long farmaciaId, Pageable pageable);


}