package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.Compra;
import farmacias.AppOchoa.model.CompraEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    // Navegación: Compra -> sucursal -> sucursalId - con paginación
    Page<Compra> findBySucursalSucursalId(Long sucursalId, Pageable pageable);

    //Navegación: Compra -> usuario -> usuarioId - con paginación
    Page<Compra> findByUsuarioUsuarioId(Long usuarioId, Pageable pageable);

    Page<Compra> findByCompraFecha(LocalDate fecha, Pageable pageable);

    //Usa el Enum CompraEstado - con paginación
    Page<Compra> findByCompraEstado(CompraEstado estado, Pageable pageable);

    Page<Compra> findByCompraFechaBetween(LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable);

    //Corrección de nombres compuestos - con paginación
    Page<Compra> findBySucursalSucursalIdAndCompraEstado(Long sucursalId, CompraEstado estado, Pageable pageable);

    Page<Compra> findBySucursalSucursalIdAndCompraFechaBetween(
            Long sucursalId, LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable);

    //Top 10 mantiene List (no necesita paginación por el límite)
    List<Compra> findTop10ByCompraEstadoOrderByCompraFechaDesc(CompraEstado estado);

    boolean existsBySucursalSucursalIdAndCompraFecha(Long sucursalId, LocalDate fecha);

    //JPQL queries mantienen List (son consultas agregadas)
    @Query("SELECT MONTH(c.compraFecha) as mes, SUM(c.compraTotal) as total " +
            "FROM Compra c " +
            "WHERE YEAR(c.compraFecha) = :anio AND c.sucursal.sucursalId = :sucursalId " +
            "GROUP BY MONTH(c.compraFecha)")
    List<Object[]> findTotalComprasPorMes(@Param("anio") int anio,
                                          @Param("sucursalId") Long sucursalId);

    @Query("SELECT c FROM Compra c WHERE c.compraTotal > :montoMinimo " +
            "AND c.compraEstado = 'activa'")
    Page<Compra> findComprasConMontoMayorA(@Param("montoMinimo") BigDecimal montoMinimo, Pageable pageable);
    Page<Compra> findByFarmacia_FarmaciaId(Long farmaciaId, Pageable pageable);
    @Query("SELECT c FROM Compra c WHERE " +
            "LOWER(c.compraObservaciones) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(c.usuario.nombreUsuarioUsuario) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(c.sucursal.sucursalNombre) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Compra> buscarPorTexto(@Param("texto") String texto, Pageable pageable);
}