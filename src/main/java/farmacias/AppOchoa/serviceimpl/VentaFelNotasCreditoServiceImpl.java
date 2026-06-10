package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.ventafelnotascredito.VentaFelNotasCreditoCreateDTO;
import farmacias.AppOchoa.dto.ventafelnotascredito.VentaFelNotasCreditoResponseDTO;
import farmacias.AppOchoa.dto.ventafelnotascredito.VentaFelNotasCreditoSimpleDTO;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.NotaEstado;
import farmacias.AppOchoa.model.VentaFel;
import farmacias.AppOchoa.model.VentaFelNotasCredito;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.repository.VentaFelNotasCreditoRepository;
import farmacias.AppOchoa.repository.VentaFelRepository;
import farmacias.AppOchoa.services.VentaFelNotasCreditoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class VentaFelNotasCreditoServiceImpl implements VentaFelNotasCreditoService {
    private final VentaFelNotasCreditoRepository ventaFelNotasCreditoRepository;
    private final VentaFelRepository ventaFelRepository;
    private final FarmaciaRepository farmaciaRepository;

    public VentaFelNotasCreditoServiceImpl(
            VentaFelNotasCreditoRepository ventaFelNotasCreditoRepository,
            VentaFelRepository ventaFelRepository,
            FarmaciaRepository farmaciaRepository){
        this.ventaFelNotasCreditoRepository = ventaFelNotasCreditoRepository;
        this.ventaFelRepository = ventaFelRepository;
        this.farmaciaRepository = farmaciaRepository;
    }
    @Override
    public VentaFelNotasCreditoResponseDTO crear(Long farmaciaId, VentaFelNotasCreditoCreateDTO dto){
        VentaFel ventaFel = buscarVentas(farmaciaId, dto.getFelId());
        Farmacia farmacia = farmaciaRepository.getReferenceById(farmaciaId);

        VentaFelNotasCredito ventaFelNotasCredito = VentaFelNotasCredito.builder()
                .ventaFel(ventaFel)
                .notaMotivo(dto.getNotaMotivo())
                .farmacia(farmacia)
                .build();

        return VentaFelNotasCreditoResponseDTO.fromEntity(ventaFelNotasCreditoRepository.save(ventaFelNotasCredito));
    }
    private VentaFel buscarVentas(Long farmaciaId, Long id){
        if(id == null) return null;
        return ventaFelRepository.findByFelIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta fel no encontrada ID"));
    }
    @Override
    @Transactional(readOnly = true)
    public VentaFelNotasCreditoResponseDTO buscarPorId(Long farmaciaId, Long id){
        return ventaFelNotasCreditoRepository.findByNotaIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .map(VentaFelNotasCreditoResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Nota credito no encontrada por ID"));
    }
    @Override
    @Transactional(readOnly = true)
    public Page<VentaFelNotasCreditoSimpleDTO> listarNotas(Long farmaciaId, Pageable pageable){
        return ventaFelNotasCreditoRepository.findByFarmacia_FarmaciaId(farmaciaId, pageable)
                .map(VentaFelNotasCreditoSimpleDTO::fromEntity);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<VentaFelNotasCreditoSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable) {
        return ventaFelNotasCreditoRepository.buscarPorTexto(farmaciaId, texto, pageable)
                .map(VentaFelNotasCreditoSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaFelNotasCreditoSimpleDTO> buscarPorFiltros(Long farmaciaId, NotaEstado estado, LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable) {
        Page<VentaFelNotasCredito> resultados;

        boolean tieneEstado = (estado != null);
        boolean tieneFechas = (fechaInicio != null && fechaFin != null);

        if (tieneEstado && tieneFechas) {
            resultados = ventaFelNotasCreditoRepository.findByFarmacia_FarmaciaIdAndNotaEstadoAndAuditoriaFechaCreacionBetween(farmaciaId, estado, fechaInicio, fechaFin, pageable);
        } else if (tieneEstado) {
            resultados = ventaFelNotasCreditoRepository.findByFarmacia_FarmaciaIdAndNotaEstado(farmaciaId, estado, pageable);
        } else if (tieneFechas) {
            resultados = ventaFelNotasCreditoRepository.findByFarmacia_FarmaciaIdAndAuditoriaFechaCreacionBetween(farmaciaId, fechaInicio, fechaFin, pageable);
        } else {
            resultados = ventaFelNotasCreditoRepository.findByFarmacia_FarmaciaId(farmaciaId, pageable);
        }

        return resultados.map(VentaFelNotasCreditoSimpleDTO::fromEntity);
    }
    @Override
    public void eliminar(Long farmaciaId, Long id) {
        throw new UnsupportedOperationException("Por reglas de auditoría financiera, este registro es histórico y no puede ser eliminado ni modificado.");
    }

}
