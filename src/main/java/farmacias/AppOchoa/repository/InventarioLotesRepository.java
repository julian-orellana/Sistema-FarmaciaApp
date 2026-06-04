package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.InventarioLotes;
import farmacias.AppOchoa.model.LoteEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface InventarioLotesRepository extends JpaRepository<InventarioLotes, Long> {

    // BÚSQUEDAS BÁSICAS
    Optional<InventarioLotes> findByProducto_ProductoIdAndSucursal_SucursalId(Long productoId, Long sucursalId);

    Page<InventarioLotes> findBySucursal_SucursalId(Long sucursalId, Pageable pageable);

    // VALIDACIONES
    boolean existsByProducto_ProductoIdAndSucursal_SucursalId(Long productoId, Long sucursalId);
    boolean existsByLoteNumeroAndSucursal_SucursalId(String loteNumero, Long sucursalId);

    // ESTADO Y STOCK
    Page<InventarioLotes> findByLoteEstado(LoteEstado estado, Pageable pageable);
    Page<InventarioLotes> findByLoteCantidadActualLessThan(Integer cantidadMinima, Pageable pageable);

    // FECHAS Y VENCIMIENTOS
    Page<InventarioLotes> findByLoteFechaVencimientoBetween(LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable);
    Page<InventarioLotes> findByLoteFechaVencimientoLessThanEqual(LocalDate fechaLimite, Pageable pageable);

    // ESCÁNER / CÓDIGO
    Optional<InventarioLotes> findByLoteNumero(String loteNumero);
    Page<InventarioLotes> findByFarmacia_FarmaciaId(Long farmaciaId, Pageable pageable);
    @Query("SELECT l FROM InventarioLotes l WHERE " +
            "LOWER(l.loteNumero) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<InventarioLotes> buscarPorTexto(@Param("texto") String texto, Pageable pageable);
}