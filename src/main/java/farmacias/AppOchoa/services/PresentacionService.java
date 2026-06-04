package farmacias.AppOchoa.services;

import farmacias.AppOchoa.dto.presentacion.PresentacionCreateDTO;
import farmacias.AppOchoa.dto.presentacion.PresentacionResponseDTO;
import farmacias.AppOchoa.dto.presentacion.PresentacionSimpleDTO;
import farmacias.AppOchoa.dto.presentacion.PresentacionUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PresentacionService {

    PresentacionResponseDTO crear(Long farmaciaId, PresentacionCreateDTO dto);
    PresentacionResponseDTO actualizar(Long farmaciaId, Long id, PresentacionUpdateDTO dto);
    void cambiarEstado(Long farmaciaId, Long id, Boolean estado);
    void eliminar(Long farmaciaId, Long id);
    PresentacionResponseDTO obtenerPorId(Long farmaciaId, Long id);
    Page<PresentacionSimpleDTO> listarActivasPaginadas(Long farmaciaId, Pageable pageable);
    Page<PresentacionSimpleDTO> listarTodasPaginadas(Long farmaciaId, Pageable pageable);
    Page<PresentacionSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable);
}