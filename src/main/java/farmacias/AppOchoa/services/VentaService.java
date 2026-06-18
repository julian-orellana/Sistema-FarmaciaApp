package farmacias.AppOchoa.services;

import farmacias.AppOchoa.dto.venta.*;
import farmacias.AppOchoa.dto.ventadetalle.VentaDetalleCreateDTO;
import farmacias.AppOchoa.exception.BadRequestException;
import farmacias.AppOchoa.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public interface VentaService {

    VentaResponseDTO crear(Long farmaciaId, VentaCreateDTO dto);
    VentaResponseDTO listarPorId(Long farmaciaId, Long id);
    VentaCobroResponseDTO crearConCobro(Long farmaciaId, VentaCreateCobroDTO dto);
    Page<VentaSimpleDTO> listarTodasPaginadas(Long farmaciaId, Pageable pageable);
    Page<VentaSimpleDTO> listarActivasPaginadas(Long farmaciaId, Pageable pageable);
    Page<VentaSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable);
    VentaResponseDTO actualizar(Long farmaciaId, Long id, VentaUpdateDTO dto);
    void cambiarEstado(Long farmaciaId, Long id, VentaEstado nuevoEstado);
    void eliminar(Long farmaciaId, Long id);
}
