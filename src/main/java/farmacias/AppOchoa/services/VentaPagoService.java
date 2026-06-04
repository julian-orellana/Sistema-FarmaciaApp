package farmacias.AppOchoa.services;

import farmacias.AppOchoa.dto.ventapago.VentaPagoCreateDTO;
import farmacias.AppOchoa.dto.ventapago.VentaPagoResponseDTO;
import farmacias.AppOchoa.dto.ventapago.VentaPagoSimpleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface VentaPagoService {
    VentaPagoResponseDTO crear(Long farmaciaId, VentaPagoCreateDTO dto);
    VentaPagoResponseDTO buscarPorId(Long farmaciaId, Long id);
    Page<VentaPagoSimpleDTO> listarActivas(Long farmaciaId, Pageable pageable);
    Page<VentaPagoSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable);
    void eliminar(Long farmaciaId, Long id);

}
