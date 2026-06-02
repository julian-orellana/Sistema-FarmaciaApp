package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.farmacia.FarmaciaCreateDTO;
import farmacias.AppOchoa.dto.farmacia.FarmaciaResponseDTO;
import farmacias.AppOchoa.dto.farmacia.FarmaciaSimpleDTO;
import farmacias.AppOchoa.exception.DuplicateResourceException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.PlanTipo;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.services.FarmaciaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FarmaciaServiceImpl implements FarmaciaService {
    private final FarmaciaRepository farmaciaRepository;

    public FarmaciaServiceImpl(FarmaciaRepository farmaciaRepository1){
        this.farmaciaRepository = farmaciaRepository1;
    }

    @Override
    public FarmaciaResponseDTO crear(FarmaciaCreateDTO dto){
        if(farmaciaRepository.existsByFarmaciaNit(dto.getFarmaciaNit())){
            throw new DuplicateResourceException("El NIT ya está en uso: " + dto.getFarmaciaNit());
        }

        Farmacia farmacia = Farmacia.builder()
                .farmaciaNombre(dto.getFarmaciaNombre())
                .farmaciaNit(dto.getFarmaciaNit())
                .farmaciaEmail(dto.getFarmaciaEmail())
                .farmaciaTelefono(dto.getFarmaciaTelefono())
                .pruebaHasta(dto.getPruebaHasta())
                .planTipo(dto.getPlanTipo())
                .suscripcionVigencia(dto.getSuscripcionVigencia())
                .farmaciaActiva(true)
                .enPeriodoPrueba(true)
                .maxSucursales(resolverMaxSucursales(dto.getPlanTipo()))
                .maxUsuarios(resolverMaxUsuarios(dto.getPlanTipo()))
                .build();


        return FarmaciaResponseDTO.fromEntity(farmaciaRepository.save(farmacia));
    }
    private int resolverMaxSucursales(PlanTipo plan) {
        return switch (plan) {
            case basico -> 1;
            case pro -> 3;
            case chain -> 10;
        };
    }

    private int resolverMaxUsuarios(PlanTipo plan) {
        return switch (plan) {
            case basico -> 2;
            case pro -> 5;
            case chain -> 20;
        };
    }

    @Override
    @Transactional(readOnly = true)
    public FarmaciaResponseDTO buscarId(Long id){
        return farmaciaRepository.findById(id)
                .map(FarmaciaResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Id no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FarmaciaSimpleDTO> listarFarmacias(Pageable pageable){
        return farmaciaRepository.findAll(pageable)
                .map(FarmaciaSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FarmaciaSimpleDTO> buscarPorTexto(String texto, Pageable pageable){
        return farmaciaRepository.buscarPorTexto(texto, pageable)
                .map(FarmaciaSimpleDTO::fromEntity);
    }

    @Override
    public void eliminar(Long id){
        throw  new UnsupportedOperationException("Por reglas de auditoría financiera, este registro es histórico y no puede ser eliminado ni modificado.");

    }

}