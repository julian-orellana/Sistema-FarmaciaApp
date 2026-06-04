package farmacias.AppOchoa.services;

import farmacias.AppOchoa.dto.ventafelnotascredito.VentaFelNotasCreditoCreateDTO;
import farmacias.AppOchoa.dto.ventafelnotascredito.VentaFelNotasCreditoResponseDTO;
import farmacias.AppOchoa.dto.ventafelnotascredito.VentaFelNotasCreditoSimpleDTO;
import farmacias.AppOchoa.model.NotaEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface VentaFelNotasCreditoService {
    VentaFelNotasCreditoResponseDTO crear(Long farmaciaId, VentaFelNotasCreditoCreateDTO dto);
    VentaFelNotasCreditoResponseDTO buscarPorId(Long farmaciaId, Long id);
    Page<VentaFelNotasCreditoSimpleDTO> listarNotas(Long farmaciaId, Pageable pageable);
    Page<VentaFelNotasCreditoSimpleDTO> buscarPorFiltros(Long farmaciaId, NotaEstado estado, LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);
    Page<VentaFelNotasCreditoSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable);
    void eliminar(Long farmaciaId, Long id);
}
