package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.alerta.AlertaCreateDTO;
import farmacias.AppOchoa.dto.alerta.AlertaResponseDTO;
import farmacias.AppOchoa.dto.alerta.AlertaSimpleDTO;
import farmacias.AppOchoa.dto.alerta.AlertaUpdateDTO;
import farmacias.AppOchoa.model.Alerta;
import farmacias.AppOchoa.model.InventarioLotes;
import farmacias.AppOchoa.model.Producto;
import farmacias.AppOchoa.model.Sucursal;
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

    public AlertaServiceImpl(
            AlertaRepository alertaRepository,
            ProductoRepository productoRepository,
            SucursalRepository sucursalRepository,
            InventarioLotesRepository inventarioLotesRepository) {
        this.alertaRepository = alertaRepository;
        this.productoRepository = productoRepository;
        this.sucursalRepository = sucursalRepository;
        this.inventarioLotesRepository = inventarioLotesRepository;
    }

    @Override
    public AlertaResponseDTO crear(Long farmaciaId, AlertaCreateDTO dto) {
        Producto producto = buscarProducto(farmaciaId, dto.getProductoId());
        Sucursal sucursal = buscarSucursal(farmaciaId, dto.getSucursalId());

        InventarioLotes lote = null;
        if (dto.getLoteId() != null) {
            lote = buscarInventarioLotes(farmaciaId, dto.getLoteId());
        }

        Alerta alerta = Alerta.builder()
                .alertaMensaje(dto.getMensaje())
                .alertaLeida(false)
                .producto(producto)
                .sucursal(sucursal)
                .lote(lote)
                .build();

        return AlertaResponseDTO.fromEntity(alertaRepository.save(alerta));
    }

    @Override
    @Transactional(readOnly = true)
    public AlertaResponseDTO listarPorId(Long farmaciaId, Long id) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada ID: " + id));
        return AlertaResponseDTO.fromEntity(alerta);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlertaSimpleDTO> listarTodasPaginadas(Long farmaciaId, Pageable pageable) {
        return alertaRepository.findAll(pageable)
                .map(AlertaSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlertaSimpleDTO> listarNoLeidasPaginadas(Long farmaciaId, Pageable pageable) {
        return alertaRepository.findByAlertaLeidaFalse(pageable)
                .map(AlertaSimpleDTO::fromEntity);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<AlertaSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable){
        return  alertaRepository.buscarPorTexto(texto, pageable)
                .map(AlertaSimpleDTO::fromEntity);
    }

    @Override
    public AlertaResponseDTO actualizar(Long farmaciaId, Long id, AlertaUpdateDTO dto) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada ID: " + id));

        if (dto.getLeida() != null) {
            alerta.setAlertaLeida(dto.getLeida());
        }

        return AlertaResponseDTO.fromEntity(alertaRepository.save(alerta));
    }

    @Override
    public void cambiarEstado(Long farmaciaId, Long id) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada ID: " + id));
        alerta.setAlertaLeida(!alerta.getAlertaLeida());
        alertaRepository.save(alerta);
    }

    @Override
    public void eliminar(Long farmaciaId, Long id) {
        if (!alertaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Alerta no encontrada ID: " + id);
        }
        alertaRepository.deleteById(id);
    }

    // MÃƒÂ©todos auxiliares privados
    private Producto buscarProducto(Long farmaciaId, Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado ID: " + id));
    }

    private Sucursal buscarSucursal(Long farmaciaId, Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada ID: " + id));
    }

    private InventarioLotes buscarInventarioLotes(Long farmaciaId, Long id) {
        return inventarioLotesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado ID: " + id));
    }
}
