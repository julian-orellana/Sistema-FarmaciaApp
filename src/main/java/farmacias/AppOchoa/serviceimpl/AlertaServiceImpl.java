package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.alerta.AlertaCreateDTO;
import farmacias.AppOchoa.dto.alerta.AlertaResponseDTO;
import farmacias.AppOchoa.dto.alerta.AlertaSimpleDTO;
import farmacias.AppOchoa.dto.alerta.AlertaUpdateDTO;
import farmacias.AppOchoa.model.*;
import farmacias.AppOchoa.repository.*;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.services.AlertaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AlertaServiceImpl implements AlertaService {

    private final AlertaRepository alertaRepository;
    private final ProductoRepository productoRepository;
    private final SucursalRepository sucursalRepository;
    private final InventarioLotesRepository inventarioLotesRepository;
    private final FarmaciaRepository farmaciaRepository;

    public AlertaServiceImpl(
            AlertaRepository alertaRepository,
            ProductoRepository productoRepository,
            SucursalRepository sucursalRepository,
            InventarioLotesRepository inventarioLotesRepository,
            FarmaciaRepository farmaciaRepository) {
        this.alertaRepository = alertaRepository;
        this.productoRepository = productoRepository;
        this.sucursalRepository = sucursalRepository;
        this.inventarioLotesRepository = inventarioLotesRepository;
        this.farmaciaRepository = farmaciaRepository;
    }

    @Override
    public AlertaResponseDTO crear(Long farmaciaId, AlertaCreateDTO dto) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        Producto producto = buscarProducto(sucursal.getSucursalId(), dto.getProductoId());
        Farmacia farmacia = farmaciaRepository.getReferenceById(farmaciaId);

        InventarioLotes lote = null;
        if (dto.getLoteId() != null) {
            lote = buscarInventarioLotes(sucursal.getSucursalId(), dto.getLoteId());
        }

        Alerta alerta = Alerta.builder()
                .alertaMensaje(dto.getMensaje())
                .alertaLeida(false)
                .producto(producto)
                .sucursal(sucursal)
                .farmacia(farmacia)
                .lote(lote)
                .build();

        return AlertaResponseDTO.fromEntity(alertaRepository.save(alerta));
    }

    @Override
    @Transactional(readOnly = true)
    public AlertaResponseDTO listarPorId(Long farmaciaId, Long id) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        Alerta alerta = alertaRepository.findByAlertaIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada ID: " + id));
        return AlertaResponseDTO.fromEntity(alerta);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlertaSimpleDTO> listarTodasPaginadas(Long farmaciaId, Pageable pageable) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return alertaRepository.findBySucursal_SucursalId(sucursal.getSucursalId(), pageable)
                .map(AlertaSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlertaSimpleDTO> listarNoLeidasPaginadas(Long farmaciaId, Pageable pageable) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return alertaRepository.findBySucursal_SucursalId(sucursal.getSucursalId(), pageable)
                .map(AlertaSimpleDTO::fromEntity);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<AlertaSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return alertaRepository.buscarPorTexto(sucursal.getSucursalId(), texto, pageable)
                .map(AlertaSimpleDTO::fromEntity);
    }

    @Override
    public AlertaResponseDTO actualizar(Long farmaciaId, Long id, AlertaUpdateDTO dto) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        Alerta alerta = alertaRepository.findByAlertaIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada ID: " + id));

        if (dto.getLeida() != null) {
            alerta.setAlertaLeida(dto.getLeida());
        }

        return AlertaResponseDTO.fromEntity(alertaRepository.save(alerta));
    }

    @Override
    public void cambiarEstado(Long farmaciaId, Long id) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        Alerta alerta = alertaRepository.findByAlertaIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada ID: " + id));
        alerta.setAlertaLeida(!alerta.getAlertaLeida());
        alertaRepository.save(alerta);
    }

    @Override
    public void eliminar(Long farmaciaId, Long id) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        Alerta alerta = alertaRepository.findByAlertaIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada ID: " + id));
        alertaRepository.delete(alerta);
    }

    // Métodos auxiliares privados
    private Producto buscarProducto(Long sucursalId, Long id) {
        return productoRepository.findById(id)
                .filter(p -> p.getSucursal().getSucursalId().equals(sucursalId))
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado en tu farmacia ID: " + id));
    }

    private Sucursal buscarSucursal(Long farmaciaId) {
        return sucursalRepository.findByFarmacia_FarmaciaId(farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada en tu farmacia ID: " ));
    }

    private InventarioLotes buscarInventarioLotes(Long sucursalId, Long id) {
        return inventarioLotesRepository.findByLoteIdAndSucursal_SucursalId(id, sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado en tu farmacia ID: " + id));
    }
}
