package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.MetodoPagoEstado;
import farmacias.AppOchoa.model.VentaPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface VentaPagoRepository extends JpaRepository<VentaPago, Long> {

    List<VentaPago> findByMetodoPago(MetodoPagoEstado metodoPagoEstado);
    List<VentaPago> findByCajaSesionesSesionId(Long sesionId);
    List<VentaPago> findByVentaVentaId(Long ventaId);

    @Query("SELECT COALESCE(SUM(vp.montoRecibido - vp.montoVuelto), 0) FROM VentaPago vp " +
            "WHERE vp.cajaSesiones.sesionId = :sesionId AND vp.metodoPago = :metodoPago")
    BigDecimal sumarPorSesionYMetodo(@Param("sesionId") Long sesionId, @Param("metodoPago") MetodoPagoEstado metodoPago);

    @Query("SELECT COALESCE(SUM(vp.montoRecibido - vp.montoVuelto), 0) FROM VentaPago vp " +
            "WHERE vp.cajaSesiones.sesionId = :sesionId")
    BigDecimal sumarTotalPorSesion(@Param("sesionId") Long sesionId);

    // Total ya abonado a una venta (neto de vuelto): para validar que un nuevo
    // pago no haga que la suma exceda el total de la venta (A9).
    @Query("SELECT COALESCE(SUM(vp.montoRecibido - vp.montoVuelto), 0) FROM VentaPago vp " +
            "WHERE vp.venta.ventaId = :ventaId")
    BigDecimal sumarAbonadoPorVenta(@Param("ventaId") Long ventaId);
    @Query("SELECT p FROM VentaPago p WHERE p.farmacia.farmaciaId = :farmaciaId AND (" +
            "LOWER(p.referenciaTransaccion) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(CAST(p.metodoPago AS string)) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(p.venta.ventaNombreCliente) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(p.venta.ventaNitCliente) LIKE LOWER(CONCAT('%', :texto, '%')))")
    Page<VentaPago> buscarPorTexto(@Param("farmaciaId") Long farmaciaId, @Param("texto") String texto, Pageable pageable);
    Page<VentaPago> findByFarmacia_FarmaciaId(Long farmaciaId, Pageable pageable);
    java.util.Optional<VentaPago> findByPagoIdAndFarmacia_FarmaciaId(Long pagoId, Long farmaciaId);
}