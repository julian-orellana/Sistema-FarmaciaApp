package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.Venta;
import farmacias.AppOchoa.model.VentaEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    // Para reimprimir factura
    Optional<Venta> findByVentaNumeroFactura(String numeroFactura);

    // Para cierre de caja del día - con paginación
    Page<Venta> findBySucursal_SucursalIdAndVentaFechaBetween(
            Long sucursalId, LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

    // Buscar por estado de venta - con paginación
    Page<Venta> findByVentaEstado(VentaEstado estado, Pageable pageable);

    // Para validar que no se repita número factura
    boolean existsByVentaNumeroFactura(String numeroFactura);

    // Para historial de cliente - con paginación
    Page<Venta> findByVentaNitCliente(String nitCliente, Pageable pageable);

    // Ventas por cajero específico - con paginación
    Page<Venta> findByUsuario_UsuarioId(Long usuarioId, Pageable pageable);

    // Ventas por cajero en turno específico - con paginación
    Page<Venta> findByUsuario_UsuarioIdAndVentaFechaBetween(
            Long usuarioId, LocalDateTime inicio, LocalDateTime fin, Pageable pageable);
    Optional<Venta> findByVentaIdAndSucursal_Farmacia_FarmaciaId(Long ventaId, Long farmaciaId);

    // Total vendido por cajero hoy (mantiene query individual)
    @Query("SELECT SUM(v.ventaTotal) FROM Venta v " +
            "WHERE v.usuario.usuarioId = :usuarioId " +
            "AND DATE(v.ventaFecha) = CURRENT_DATE " +
            "AND v.ventaEstado = 'completada'")
    Double findTotalVendidoHoyPorUsuario(@Param("usuarioId") Long usuarioId);
    Page<Venta> findByFarmacia_FarmaciaId(Long farmaciaId, Pageable pageable);
    @Query("SELECT v FROM Venta v WHERE " +
            "LOWER(v.ventaNitCliente) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(v.ventaNombreCliente) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(v.ventaNumeroFactura) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(v.usuario.nombreUsuarioUsuario) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Venta> buscarPorTexto(@Param("texto") String texto, Pageable pageable);
}