package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.Caja;
import farmacias.AppOchoa.model.CajaEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CajaRepository extends JpaRepository<Caja, Long> {
    boolean existsByCajaNombre(String caja);
    List<Caja> findByCajaId(Long id);
    boolean existsBySucursalSucursalIdAndCajaNombre(Long sucursalId, String cajaNombre);
    Page<Caja> findByCajaEstado(CajaEstado cajaEstado, Pageable pageable);

    @Query("SELECT c FROM Caja c WHERE " +
            "LOWER(c.cajaNombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(CAST(c.cajaEstado AS string)) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Caja> buscarPorTexto(@Param("texto") String texto, Pageable pageable);

    @Query("SELECT c FROM Caja c WHERE c.sucursal.sucursalId = :sucursalId AND (" +
            "LOWER(c.cajaNombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(CAST(c.cajaEstado AS string)) LIKE LOWER(CONCAT('%', :texto, '%')))")
    Page<Caja> buscarPorTexto(@Param("sucursalId") Long sucursalId, @Param("texto") String texto, Pageable pageable);

    Page<Caja> findByFarmacia_FarmaciaId(Long farmaciaId, Pageable pageable);
    Optional<Caja> findByCajaIdAndFarmacia_FarmaciaId(Long cajaId, Long farmaciaId);
    Optional<Caja> findByCajaIdAndSucursal_SucursalId(Long cajaId, Long sucursalId);
    Page<Caja> findBySucursal_SucursalIdAndCajaEstado(Long sucursalId, CajaEstado cajaEstado, Pageable pageable);
}