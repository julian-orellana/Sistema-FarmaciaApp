package farmacias.AppOchoa.services;

import farmacias.AppOchoa.dto.inventariolotes.InventarioLotesCreateDTO;
import farmacias.AppOchoa.dto.inventariolotes.InventarioLotesResponseDTO;
import farmacias.AppOchoa.dto.inventariolotes.InventarioLotesSimpleDTO;
import farmacias.AppOchoa.dto.inventariolotes.InventarioLotesUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface InventarioLotesService {
    InventarioLotesResponseDTO crear(Long farmaciaId, InventarioLotesCreateDTO dto);
    Page<InventarioLotesSimpleDTO> listarPorSucursalPaginado(Long farmaciaId, Long sucursalId, Pageable pageable);
    InventarioLotesResponseDTO buscarPorId(Long farmaciaId, Long id);
    Page<InventarioLotesSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable);
    InventarioLotesResponseDTO actualizar(Long farmaciaId, Long id, InventarioLotesUpdateDTO dto);
    Page<InventarioLotesSimpleDTO> listarProximosAVencerPaginado(Long farmaciaId, LocalDate fechaLimite, Pageable pageable);
    void eliminar(Long farmaciaId, Long id);
}