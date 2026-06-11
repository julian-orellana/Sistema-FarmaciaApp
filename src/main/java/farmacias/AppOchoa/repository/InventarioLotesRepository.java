package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.InventarioLotes;
import farmacias.AppOchoa.model.LoteEstado;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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
    Page<InventarioLotes> findBySucursal_SucursalIdAndFarmacia_FarmaciaId(Long sucursalId, Long farmaciaId, Pageable pageable);

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

    // Un lote pertenece a un producto en una sucursal: el numero de lote por si
    // solo no es unico entre farmacias/productos. Scopear evita sumar stock al
    // lote de otra farmacia que comparta el mismo numero (A2).
    Optional<InventarioLotes> findByLoteNumeroAndSucursal_SucursalIdAndProducto_ProductoId(
            String loteNumero, Long sucursalId, Long productoId);

    boolean existsByLoteNumeroAndSucursal_SucursalIdAndProducto_ProductoId(
            String loteNumero, Long sucursalId, Long productoId);
    Page<InventarioLotes> findByFarmacia_FarmaciaId(Long farmaciaId, Pageable pageable);
    Optional<InventarioLotes> findByLoteIdAndFarmacia_FarmaciaId(Long loteId, Long farmaciaId);

    // SELECT ... FOR UPDATE: bloquea la fila del lote para que dos ventas
    // simultáneas no descuenten el mismo stock (la segunda espera y ve la cantidad ya descontada)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM InventarioLotes l WHERE l.loteId = :loteId AND l.farmacia.farmaciaId = :farmaciaId")
    Optional<InventarioLotes> findByLoteIdAndFarmaciaIdForUpdate(@Param("loteId") Long loteId, @Param("farmaciaId") Long farmaciaId);
    @Query("SELECT l FROM InventarioLotes l WHERE " +
            "LOWER(l.loteNumero) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<InventarioLotes> buscarPorTexto(@Param("texto") String texto, Pageable pageable);

    @Query("SELECT l FROM InventarioLotes l WHERE l.farmacia.farmaciaId = :farmaciaId AND " +
            "LOWER(l.loteNumero) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<InventarioLotes> buscarPorTexto(@Param("farmaciaId") Long farmaciaId, @Param("texto") String texto, Pageable pageable);
}