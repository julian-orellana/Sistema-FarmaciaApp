package farmacias.AppOchoa.services;

import farmacias.AppOchoa.dto.alerta.AlertaCreateDTO;
import farmacias.AppOchoa.dto.alerta.AlertaResponseDTO;
import farmacias.AppOchoa.dto.alerta.AlertaSimpleDTO;
import farmacias.AppOchoa.dto.alerta.AlertaUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlertaService {

    AlertaResponseDTO crear(Long farmaciaId, AlertaCreateDTO dto);
    AlertaResponseDTO listarPorId(Long farmaciaId, Long id);
    Page<AlertaSimpleDTO> listarTodasPaginadas(Long farmaciaId, Pageable pageable);
    Page<AlertaSimpleDTO> listarNoLeidasPaginadas(Long farmaciaId, Pageable pageable);
    Page<AlertaSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable);
    AlertaResponseDTO actualizar(Long farmaciaId, Long id, AlertaUpdateDTO dto);
    void cambiarEstado(Long farmaciaId, Long id);
    void eliminar(Long farmaciaId, Long id);
}