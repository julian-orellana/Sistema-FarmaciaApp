package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.suscripcionpago.SuscripcionPagoCreateDTO;
import farmacias.AppOchoa.dto.suscripcionpago.SuscripcionPagoResponseDTO;
import farmacias.AppOchoa.dto.suscripcionpago.SuscripcionPagoSimpleDTO;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.PagoEstado;
import farmacias.AppOchoa.model.SuscripcionPago;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.repository.SuscripcionPagoRepository;
import farmacias.AppOchoa.services.SuscripcionPagoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SuscripcionPagoServiceImpl implements SuscripcionPagoService {
    private final SuscripcionPagoRepository suscripcionPagoRepository;
    private final FarmaciaRepository farmaciaRepository;

    public SuscripcionPagoServiceImpl(
            SuscripcionPagoRepository suscripcionPagoRepository,
            FarmaciaRepository farmaciaRepository){
        this.suscripcionPagoRepository = suscripcionPagoRepository;
        this.farmaciaRepository = farmaciaRepository;
    }
    @Override
    public SuscripcionPagoResponseDTO crear(Long farmaciaId, SuscripcionPagoCreateDTO dto){
        Farmacia farmacia = buscarFarmacia(farmaciaId);

        SuscripcionPago suscripcionPago = SuscripcionPago.builder()
                .pagoMonto(dto.getPagoMonto())
                .pagoMetodo(dto.getPagoMetodo())
                .pagoPlan(dto.getPagoPlan())
                .pagoReferencia(dto.getPagoReferencia())
                .pagoPeriodoInicio(dto.getPagoPeriodoInicio())
                .pagoPeriodoFin(dto.getPagoPeriodoFin())
                .pagoNotas(dto.getPagoNotas())
                .farmacia(farmacia)
                .pagoEstado(PagoEstado.pendiente)
                .build();

        return SuscripcionPagoResponseDTO.fromEntity(suscripcionPagoRepository.save(suscripcionPago));
    }

    private Farmacia buscarFarmacia(Long farmaciaId) {
        if (farmaciaId == null) return null;
        return farmaciaRepository.findById(farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmacia no encontrada por Id"));
    }

    @Override
    @Transactional(readOnly = true)
    public SuscripcionPagoResponseDTO buscarPorId(Long farmaciaId, Long id){
        return suscripcionPagoRepository.findById(id)
                .map(SuscripcionPagoResponseDTO::fromEntity)
                .orElseThrow(()-> new ResourceNotFoundException("Suscripcion no encontrada por Id"));

    }
    @Override
    @Transactional(readOnly = true)
    public Page<SuscripcionPagoSimpleDTO> listarSuscripciones(Long farmaciaId, Pageable pageable){
        return suscripcionPagoRepository.findAll(pageable)
                .map(SuscripcionPagoSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SuscripcionPagoSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable){
        return suscripcionPagoRepository.buscarPorTexto(farmaciaId,texto, pageable)
                .map(SuscripcionPagoSimpleDTO::fromEntity);
    }

    @Override
    public void eliminar(Long farmaciaId, Long id){
        throw new UnsupportedOperationException("Por reglas de auditoría financiera, este registro es histórico y no puede ser eliminado ni modificado.");
    }


}
