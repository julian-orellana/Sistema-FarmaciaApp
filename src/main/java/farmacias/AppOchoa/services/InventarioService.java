package farmacias.AppOchoa.services;

import farmacias.AppOchoa.dto.inventario.InventarioCreateDTO;
import farmacias.AppOchoa.dto.inventario.InventarioResponseDTO;
import farmacias.AppOchoa.dto.inventario.InventarioSimpleDTO;
import farmacias.AppOchoa.dto.inventario.InventarioUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventarioService {

    InventarioResponseDTO crear(Long farmaciaId, InventarioCreateDTO dto);
    InventarioResponseDTO listaPorId(Long farmaciaId, Long id);
    Page<InventarioSimpleDTO> listarTodosPaginado(Long farmaciaId, Pageable pageable);
    Page<InventarioSimpleDTO> listarActivosPaginado(Long farmaciaId, Pageable pageable);
    Page<InventarioSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable);
    InventarioResponseDTO actualizar(Long farmaciaId, Long id, InventarioUpdateDTO dto);
    void eliminar(Long farmaciaId, Long id);
}