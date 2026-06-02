package farmacias.AppOchoa.services;

import farmacias.AppOchoa.dto.cajacorte.CajaCorteCreateDTO;
import farmacias.AppOchoa.dto.cajacorte.CajaCorteResponseDTO;
import farmacias.AppOchoa.dto.cajacorte.CajaCorteSimpleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CajaCorteService {
    CajaCorteResponseDTO crear(Long farmaciaId, CajaCorteCreateDTO dto);
    CajaCorteResponseDTO buscarPorId(Long farmaciaId, Long id);
    Page<CajaCorteSimpleDTO> listarCortes(Long farmaciaId, Pageable pageable);
    Page<CajaCorteSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable);
    void eliminar(Long farmaciaId, Long id);
}
