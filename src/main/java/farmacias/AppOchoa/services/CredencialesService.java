package farmacias.AppOchoa.services;

import farmacias.AppOchoa.dto.credencialesfel.CredencialesCreateDTO;
import farmacias.AppOchoa.dto.credencialesfel.CredencialesResponseDTO;
import farmacias.AppOchoa.dto.credencialesfel.CredencialesSimpleDTO;
import farmacias.AppOchoa.dto.credencialesfel.CredencialesUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CredencialesService {
    CredencialesResponseDTO crear(Long farmaciaId, CredencialesCreateDTO dto);
    CredencialesResponseDTO actualizar(Long farmaciaId, Long id, CredencialesUpdateDTO dto);
    CredencialesResponseDTO buscarPorId(Long farmaciaId, Long id);
    List<CredencialesSimpleDTO> listar(Long farmaciaId);
    void eliminar(Long farmaciaId, Long id);
}
