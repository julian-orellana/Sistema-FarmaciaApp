package farmacias.AppOchoa.services;

import farmacias.AppOchoa.dto.categoria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CategoriaService {
    CategoriaResponseDTO crear(Long farmaciaId, CategoriaCreateDTO dto);
    CategoriaResponseDTO actualizar(Long farmaciaId, Long id, CategoriaUpdateDTO dto);
    void cambiarEstado(Long farmaciaId, Long id, Boolean estado);
    void eliminar(Long farmaciaId, Long id);
    CategoriaResponseDTO obtenerPorId(Long farmaciaId, Long id);
    Page<CategoriaSimpleDTO> listarActivasPaginadas(Long farmaciaId, Pageable pageable);
    Page<CategoriaSimpleDTO> listarTodasPaginadas(Long farmaciaId, Pageable pageable);
    Page<CategoriaSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable);
}