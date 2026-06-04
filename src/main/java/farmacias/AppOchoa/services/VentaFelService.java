package farmacias.AppOchoa.services;

import farmacias.AppOchoa.dto.ventafel.VentaFelCreateDTO;
import farmacias.AppOchoa.dto.ventafel.VentaFelResponseDTO;
import farmacias.AppOchoa.dto.ventafel.VentaFelSimpleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VentaFelService {
    VentaFelResponseDTO crear(Long farmaciaId, VentaFelCreateDTO dto);
    VentaFelResponseDTO buscarPorId(Long farmaciaId, Long id);
    Page<VentaFelSimpleDTO> listarActivas(Long farmaciaId, Pageable pageable);
    Page<VentaFelSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable);
    void eliminar(Long farmaciaId, Long id);
}
