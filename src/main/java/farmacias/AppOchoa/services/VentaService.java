package farmacias.AppOchoa.services;

import farmacias.AppOchoa.dto.venta.VentaCreateDTO;
import farmacias.AppOchoa.dto.venta.VentaResponseDTO;
import farmacias.AppOchoa.dto.venta.VentaSimpleDTO;
import farmacias.AppOchoa.dto.venta.VentaUpdateDTO;
import farmacias.AppOchoa.model.VentaEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VentaService {

    VentaResponseDTO crear(Long farmaciaId, VentaCreateDTO dto);
    VentaResponseDTO listarPorId(Long farmaciaId, Long id);
    Page<VentaSimpleDTO> listarTodasPaginadas(Long farmaciaId, Pageable pageable);
    Page<VentaSimpleDTO> listarActivasPaginadas(Long farmaciaId, Pageable pageable);
    Page<VentaSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable);
    VentaResponseDTO actualizar(Long farmaciaId, Long id, VentaUpdateDTO dto);
    void cambiarEstado(Long farmaciaId, Long id, VentaEstado nuevoEstado);
    void eliminar(Long farmaciaId, Long id);
}