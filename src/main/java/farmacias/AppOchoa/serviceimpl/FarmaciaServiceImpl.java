package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.farmacia.FarmaciaCreateDTO;
import farmacias.AppOchoa.dto.farmacia.FarmaciaResponseDTO;
import farmacias.AppOchoa.dto.farmacia.FarmaciaSimpleDTO;
import farmacias.AppOchoa.dto.farmacia.SuscripcionRenovarDTO;
import farmacias.AppOchoa.exception.DuplicateResourceException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.PlanTipo;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.services.FarmaciaService;
import farmacias.AppOchoa.services.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FarmaciaServiceImpl implements FarmaciaService {
    private final FarmaciaRepository farmaciaRepository;
    private final UsuarioService usuarioService;

    public FarmaciaServiceImpl(FarmaciaRepository farmaciaRepository1,
                               UsuarioService usuarioService){
        this.farmaciaRepository = farmaciaRepository1;
        this.usuarioService = usuarioService;
    }

    @Override
    public FarmaciaResponseDTO crear(FarmaciaCreateDTO dto){
        if(farmaciaRepository.existsByFarmaciaTelefono(dto.getFarmaciaTelefono())){
            throw new DuplicateResourceException("El teléfono ya está en uso: " + dto.getFarmaciaTelefono());
        }

        Farmacia farmacia = Farmacia.builder()
                .farmaciaNombre(dto.getFarmaciaNombre())
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


        Farmacia farmaciaGuardada = farmaciaRepository.save(farmacia);
        usuarioService.crearAdminInicial(dto.getAdminInicial(), farmaciaGuardada);
        return FarmaciaResponseDTO.fromEntity(farmaciaGuardada);
    }
    private int resolverMaxSucursales(PlanTipo plan) {
        return switch (plan) {
            case basico -> 1;
        };
    }

    private int resolverMaxUsuarios(PlanTipo plan) {
        return switch (plan) {
            case basico -> 3;
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
    public FarmaciaResponseDTO renovarSuscripcion(Long id, SuscripcionRenovarDTO dto){
        Farmacia farmacia = farmaciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farmacia no encontrada ID: " + id));

        farmacia.setSuscripcionVigencia(dto.getNuevaVigencia());
        farmacia.setEnPeriodoPrueba(false);
        farmacia.setFarmaciaActiva(true);

        return FarmaciaResponseDTO.fromEntity(farmaciaRepository.save(farmacia));
    }

    @Override
    public void eliminar(Long id){
        throw  new UnsupportedOperationException("Por reglas de auditoría financiera, este registro es histórico y no puede ser eliminado ni modificado.");

    }

}