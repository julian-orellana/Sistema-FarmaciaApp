package farmacias.AppOchoa.services;

import farmacias.AppOchoa.dto.autorizacion.AutorizacionCreateDTO;
import farmacias.AppOchoa.dto.autorizacion.AutorizacionResponseDTO;
import farmacias.AppOchoa.dto.autorizacion.AutorizacionSimpleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface AutorizacionService {
    AutorizacionResponseDTO crear(Long farmaciaId, AutorizacionCreateDTO dto);
    AutorizacionResponseDTO buscarPorId(Long farmaciaId, Long id);
    Page<AutorizacionSimpleDTO> listarTodas(Long farmaciaId, Pageable pageable);
    Page<AutorizacionSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable);
    void eliminar(Long farmaciaId, Long id);
}
