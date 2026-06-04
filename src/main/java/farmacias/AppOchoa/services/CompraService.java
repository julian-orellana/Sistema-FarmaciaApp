package farmacias.AppOchoa.services;

import farmacias.AppOchoa.dto.compra.CompraCreateDTO;
import farmacias.AppOchoa.dto.compra.CompraResponseDTO;
import farmacias.AppOchoa.dto.compra.CompraSimpleDTO;
import farmacias.AppOchoa.dto.compra.CompraUpdateDTO;
import farmacias.AppOchoa.model.CompraEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompraService {

    CompraResponseDTO crear(Long farmaciaId, CompraCreateDTO dto);
    CompraResponseDTO listarPorId(Long farmaciaId, Long id);
    Page<CompraSimpleDTO> listarTodasPaginadas(Long farmaciaId, Pageable pageable);
    Page<CompraSimpleDTO> listarActivasPaginadas(Long farmaciaId, Pageable pageable);
    Page<CompraSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable);
    CompraResponseDTO actualizar(Long farmaciaId, Long id, CompraUpdateDTO dto);
    void cambiarEstado(Long farmaciaId, Long id, CompraEstado compraEstado);
    void eliminar(Long farmaciaId, Long id);
}