package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.Inventario;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    // Buscar inventario por producto y sucursal
    Optional<Inventario> findByProducto_ProductoIdAndSucursal_SucursalId(Long productoId, Long sucursalId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventario i WHERE i.producto.productoId = :productoId " +
            "AND i.sucursal.sucursalId = :sucursalId")
    Optional<Inventario> findByProductoYSucursalForUpdate(@Param("productoId") Long productoId,
                                                          @Param("sucursalId") Long sucursalId);

    // Consultar stock bajo o igual a cierta cantidad - con paginación
    @Query("SELECT i FROM Inventario i WHERE i.inventarioCantidadActual <= :cantidad")
    Page<Inventario> findByInventarioCantidadActualLessThanEqual(@Param("cantidad") Integer cantidad, Pageable pageable);

    // Buscar todos los inventarios de una sucursal específica - con paginación
    Page<Inventario> findBySucursal_SucursalId(Long sucursalId, Pageable pageable);

    // Verificar si existe un producto en una sucursal
    boolean existsByProducto_ProductoIdAndSucursal_SucursalId(Long productoId, Long sucursalId);
    List<Inventario> findByFarmacia_FarmaciaId(Long farmaciaId);

    // Buscar productos con stock bajo el mínimo - con paginación
    @Query("SELECT i FROM Inventario i WHERE i.inventarioCantidadActual < i.inventarioCantidadMinima")
    Page<Inventario> findByInventarioCantidadActualLessThanInventarioCantidadMinima(Pageable pageable);

    // Buscar por ID de inventario
    Optional<Inventario> findByInventarioId(Long inventarioId);
    List<Inventario> findByFarmacia_FarmaciaIdAndSucursal_SucursalId(Long farmaciaId, Long sucursalId);
    List<Inventario> findByFarmacia_FarmaciaIdAndSucursal_SucursalIdAndProducto_Categoria_CategoriaId(
            Long farmaciaId, Long sucursalId, Long categoriaId);

    // Para listar activos (con stock mayor a 0)
    @Query("SELECT i FROM Inventario i WHERE i.inventarioCantidadActual > 0")
    Page<Inventario> findActivosPaginado(Pageable pageable);
    Page<Inventario> findByFarmacia_FarmaciaId(Long farmaciaId, Pageable pageable);
    Optional<Inventario> findByInventarioIdAndFarmacia_FarmaciaId(Long inventarioId, Long farmaciaId);
    @Query("SELECT i FROM Inventario i WHERE " +
            "LOWER(i.producto.productoNombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(i.sucursal.sucursalNombre) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Inventario> buscarPorTexto(@Param("texto") String texto, Pageable pageable);

    @Query("SELECT i FROM Inventario i WHERE i.farmacia.farmaciaId = :farmaciaId AND (" +
            "LOWER(i.producto.productoNombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(i.sucursal.sucursalNombre) LIKE LOWER(CONCAT('%', :texto, '%')))")
    Page<Inventario> buscarPorTexto(@Param("farmaciaId") Long farmaciaId, @Param("texto") String texto, Pageable pageable);
}