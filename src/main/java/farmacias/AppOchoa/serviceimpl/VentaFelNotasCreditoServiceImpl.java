package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.ventafelnotascredito.VentaFelNotasCreditoCreateDTO;
import farmacias.AppOchoa.dto.ventafelnotascredito.VentaFelNotasCreditoResponseDTO;
import farmacias.AppOchoa.dto.ventafelnotascredito.VentaFelNotasCreditoSimpleDTO;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.NotaEstado;
import farmacias.AppOchoa.model.Sucursal;
import farmacias.AppOchoa.model.VentaFel;
import farmacias.AppOchoa.model.VentaFelNotasCredito;
import farmacias.AppOchoa.repository.SucursalRepository;
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
    private final SucursalRepository sucursalRepository;

    public VentaFelNotasCreditoServiceImpl(
            VentaFelNotasCreditoRepository ventaFelNotasCreditoRepository,
            VentaFelRepository ventaFelRepository,
            SucursalRepository sucursalRepository){
        this.ventaFelNotasCreditoRepository = ventaFelNotasCreditoRepository;
        this.ventaFelRepository = ventaFelRepository;
        this.sucursalRepository = sucursalRepository;
    }

    private Sucursal buscarSucursal(Long farmaciaId){
        return sucursalRepository.findByFarmacia_FarmaciaId(farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada para tu farmacia"));
    }

    @Override
    public VentaFelNotasCreditoResponseDTO crear(Long farmaciaId, VentaFelNotasCreditoCreateDTO dto){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        VentaFel ventaFel = buscarVentas(sucursal.getSucursalId(), dto.getFelId());

        VentaFelNotasCredito ventaFelNotasCredito = VentaFelNotasCredito.builder()
                .ventaFel(ventaFel)
                .notaMotivo(dto.getNotaMotivo())
                .farmacia(sucursal.getFarmacia())
                .sucursal(sucursal)
                .build();

        return VentaFelNotasCreditoResponseDTO.fromEntity(ventaFelNotasCreditoRepository.save(ventaFelNotasCredito));
    }
    private VentaFel buscarVentas(Long sucursalId, Long id){
        if(id == null) return null;
        return ventaFelRepository.findByFelIdAndSucursal_SucursalId(id, sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta fel no encontrada ID"));
    }
    @Override
    @Transactional(readOnly = true)
    public VentaFelNotasCreditoResponseDTO buscarPorId(Long farmaciaId, Long id){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return ventaFelNotasCreditoRepository.findByNotaIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .map(VentaFelNotasCreditoResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Nota credito no encontrada por ID"));
    }
    @Override
    @Transactional(readOnly = true)
    public Page<VentaFelNotasCreditoSimpleDTO> listarNotas(Long farmaciaId, Pageable pageable){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return ventaFelNotasCreditoRepository.findBySucursal_SucursalId(sucursal.getSucursalId(), pageable)
                .map(VentaFelNotasCreditoSimpleDTO::fromEntity);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<VentaFelNotasCreditoSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return ventaFelNotasCreditoRepository.buscarPorTexto(sucursal.getSucursalId(), texto, pageable)
                .map(VentaFelNotasCreditoSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaFelNotasCreditoSimpleDTO> buscarPorFiltros(Long farmaciaId, NotaEstado estado, LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        Long sucursalId = sucursal.getSucursalId();
        Page<VentaFelNotasCredito> resultados;

        boolean tieneEstado = (estado != null);
        boolean tieneFechas = (fechaInicio != null && fechaFin != null);

        if (tieneEstado && tieneFechas) {
            resultados = ventaFelNotasCreditoRepository.findBySucursal_SucursalIdAndNotaEstadoAndAuditoriaFechaCreacionBetween(sucursalId, estado, fechaInicio, fechaFin, pageable);
        } else if (tieneEstado) {
            resultados = ventaFelNotasCreditoRepository.findBySucursal_SucursalIdAndNotaEstado(sucursalId, estado, pageable);
        } else if (tieneFechas) {
            resultados = ventaFelNotasCreditoRepository.findBySucursal_SucursalIdAndAuditoriaFechaCreacionBetween(sucursalId, fechaInicio, fechaFin, pageable);
        } else {
            resultados = ventaFelNotasCreditoRepository.findBySucursal_SucursalId(sucursalId, pageable);
        }

        return resultados.map(VentaFelNotasCreditoSimpleDTO::fromEntity);
    }
    @Override
    public void eliminar(Long farmaciaId, Long id) {
        throw new UnsupportedOperationException("Por reglas de auditoría financiera, este registro es histórico y no puede ser eliminado ni modificado.");
    }

}
