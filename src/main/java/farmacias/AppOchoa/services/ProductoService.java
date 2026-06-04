package farmacias.AppOchoa.services;

import farmacias.AppOchoa.dto.producto.ProductoCreateDTO;
import farmacias.AppOchoa.dto.producto.ProductoResponseDTO;
import farmacias.AppOchoa.dto.producto.ProductoSimpleDTO;
import farmacias.AppOchoa.dto.producto.ProductoUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductoService {
    ProductoResponseDTO agregarProducto(Long farmaciaId, ProductoCreateDTO dto);
    ProductoResponseDTO actualizarProducto(Long farmaciaId, Long id, ProductoUpdateDTO dto);
    void eliminarProducto(Long farmaciaId, Long id);
    void cambiarEstado(Long farmaciaId, Long id, Boolean nuevoEstado);
    ProductoResponseDTO obtenerPorCodigoBarras(Long farmaciaId, String productoCodigoBarras);
    ProductoResponseDTO obtenerPorId(Long farmaciaId, Long id);
    Page<ProductoSimpleDTO> listarProductosActivos(Long farmaciaId, Pageable pageable);
    Page<ProductoSimpleDTO> buscarPorTexto(Long farmaciaID, String texto, Pageable pageable);
}