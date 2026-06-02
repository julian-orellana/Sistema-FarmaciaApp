package farmacias.AppOchoa.services;

import farmacias.AppOchoa.dto.caja.CajaCreateDTO;
import farmacias.AppOchoa.dto.caja.CajaResponseDTO;
import farmacias.AppOchoa.dto.caja.CajaSimpleDTO;
import farmacias.AppOchoa.dto.caja.CajaUpdateDTO;
import farmacias.AppOchoa.model.CajaEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CajaService {
    CajaResponseDTO crearCaja(Long farmaciaId, CajaCreateDTO dto);
    CajaResponseDTO actualizarCaja(Long farmaciaId, Long id, CajaUpdateDTO dto);
    CajaResponseDTO buscarPorId(Long farmaciaId, Long id);
    Page<CajaSimpleDTO> listarCajasActivas(Long farmaciaId, Pageable pageable);
    Page<CajaSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable);
    void cambiarEstado(Long farmaciaId, Long id, CajaEstado cajaEstado);
    void eliminar(Long farmaciaId, Long id);
}
