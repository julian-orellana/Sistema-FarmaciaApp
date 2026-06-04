package farmacias.AppOchoa.services;

import farmacias.AppOchoa.dto.suscripcionpago.SuscripcionPagoCreateDTO;
import farmacias.AppOchoa.dto.suscripcionpago.SuscripcionPagoResponseDTO;
import farmacias.AppOchoa.dto.suscripcionpago.SuscripcionPagoSimpleDTO;
import farmacias.AppOchoa.repository.SuscripcionPagoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SuscripcionPagoService {
    SuscripcionPagoResponseDTO crear(Long farmaciaId, SuscripcionPagoCreateDTO dto);
    SuscripcionPagoResponseDTO buscarPorId(Long farmaciaId, Long id);
    Page<SuscripcionPagoSimpleDTO> listarSuscripciones(Long farmaciaId, Pageable pageable);
    void eliminar(Long farmaciaId, Long id);
    Page<SuscripcionPagoSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable);
}
