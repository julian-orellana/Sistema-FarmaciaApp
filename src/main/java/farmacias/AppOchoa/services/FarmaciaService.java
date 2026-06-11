package farmacias.AppOchoa.services;

import farmacias.AppOchoa.dto.farmacia.FarmaciaCreateDTO;
import farmacias.AppOchoa.dto.farmacia.FarmaciaResponseDTO;
import farmacias.AppOchoa.dto.farmacia.FarmaciaSimpleDTO;
import farmacias.AppOchoa.dto.farmacia.SuscripcionRenovarDTO;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FarmaciaService {
    FarmaciaResponseDTO crear (FarmaciaCreateDTO dto);
    FarmaciaResponseDTO buscarId(Long id);
    Page<FarmaciaSimpleDTO> listarFarmacias(Pageable pageable);
    void eliminar(Long id);
    Page<FarmaciaSimpleDTO> buscarPorTexto(String texto, Pageable pageable);
    FarmaciaResponseDTO renovarSuscripcion(Long id, SuscripcionRenovarDTO dto);

}
