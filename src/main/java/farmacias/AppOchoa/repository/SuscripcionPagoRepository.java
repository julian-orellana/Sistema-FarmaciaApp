package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.SuscripcionPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SuscripcionPagoRepository extends JpaRepository<SuscripcionPago, Long> {
    Page<SuscripcionPago> findByFarmacia_FarmaciaId(Long farmaciaId, Pageable pageable);

    Optional<SuscripcionPago> findByFarmacia_FarmaciaIdAndPagoId(Long farmaciaId, Long pagoId);

    @Query("SELECT s FROM SuscripcionPago s WHERE s.farmacia.farmaciaId = :farmaciaId AND " +
            "LOWER(s.pagoPlan) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<SuscripcionPago> buscarPorTexto(@Param("farmaciaId") Long farmaciaId,
                                         @Param("texto") String texto,
                                         Pageable pageable);

}
