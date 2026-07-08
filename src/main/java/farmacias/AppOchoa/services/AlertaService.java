package farmacias.AppOchoa.services;

import farmacias.AppOchoa.dto.alerta.AlertaCreateDTO;
import farmacias.AppOchoa.dto.alerta.AlertaResponseDTO;
import farmacias.AppOchoa.dto.alerta.AlertaSimpleDTO;
import farmacias.AppOchoa.dto.alerta.AlertaUpdateDTO;
import farmacias.AppOchoa.model.Sucursal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlertaService {

    AlertaResponseDTO crear(Long sucursalId, AlertaCreateDTO dto);
    AlertaResponseDTO listarPorId(Long sucursalId, Long id);
    Page<AlertaSimpleDTO> listarTodasPaginadas(Long sucursalId, Pageable pageable);
    Page<AlertaSimpleDTO> listarNoLeidasPaginadas(Long sucursalId, Pageable pageable);
    Page<AlertaSimpleDTO> buscarPorTexto(Long sucursalId, String texto, Pageable pageable);
    AlertaResponseDTO actualizar(Long sucursalId, Long id, AlertaUpdateDTO dto);
    void cambiarEstado(Long sucursalId, Long id);
    void eliminar(Long sucursalId, Long id);
}