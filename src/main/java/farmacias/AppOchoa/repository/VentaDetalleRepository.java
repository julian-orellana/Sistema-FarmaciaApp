package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.dto.dashboard.ProductoVendidoDTO;
import farmacias.AppOchoa.model.VentaDetalle;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;

public interface VentaDetalleRepository extends JpaRepository<VentaDetalle, Long> {

    @Query("SELECT SUM(vd.detalleCantidad) FROM VentaDetalle vd WHERE vd.venta.farmacia.farmaciaId = :farmaciaId AND " +
            "vd.venta.sucursal.sucursalId = :sucursalId AND vd.venta.ventaFecha BETWEEN :fechaInicio AND :fechaFin AND vd.venta.ventaEstado = farmacias.AppOchoa.model.VentaEstado.completada")
    Integer sumarUnidadesVendidas(
    @Param("farmaciaId") Long farmaciaId,
    @Param("sucursalId") Long sucursalId,
    @Param("fechaInicio") LocalDateTime fechaInicio,
    @Param("fechaFin") LocalDateTime fechaFin);


    @Query("SELECT new farmacias.AppOchoa.dto.dashboard.ProductoVendidoDTO(" +
            "vd.producto.productoId, vd.producto.productoNombre, SUM(vd.detalleCantidad)) " +
            "FROM VentaDetalle vd " +
            "WHERE vd.venta.farmacia.farmaciaId = :farmaciaId AND " +
            "vd.venta.sucursal.sucursalId = :sucursalId AND " +
            "vd.venta.ventaFecha BETWEEN :fechaInicio AND :fechaFin AND " +
            "vd.venta.ventaEstado = farmacias.AppOchoa.model.VentaEstado.completada " +
            "GROUP BY vd.producto.productoId, vd.producto.productoNombre " +
            "ORDER BY SUM(vd.detalleCantidad) DESC")
    List<ProductoVendidoDTO> productosMasVendidos(
            @Param("farmaciaId") Long farmaciaId,
            @Param("sucursalId") Long sucursalId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable);


}
