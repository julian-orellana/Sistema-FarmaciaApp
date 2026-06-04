package farmacias.AppOchoa.services;


import farmacias.AppOchoa.dto.cajasesiones.CajaSesionesCreateDTO;
import farmacias.AppOchoa.dto.cajasesiones.CajaSesionesResponseDTO;
import farmacias.AppOchoa.dto.cajasesiones.CajaSesionesSimpleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CajaSesionesService {
    CajaSesionesResponseDTO crear(Long farmaciaId, CajaSesionesCreateDTO dto);
    CajaSesionesResponseDTO buscarPorId(Long farmaciaId, Long id);
    Page<CajaSesionesSimpleDTO> listarSesiones(Long farmaciaId, Pageable pageable);
    Page<CajaSesionesSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable);
    void eliminar(Long farmaciaId, Long id);
}
