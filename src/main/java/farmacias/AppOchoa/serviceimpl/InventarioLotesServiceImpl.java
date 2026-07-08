package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.inventariolotes.InventarioLotesCreateDTO;
import farmacias.AppOchoa.dto.inventariolotes.InventarioLotesResponseDTO;
import farmacias.AppOchoa.dto.inventariolotes.InventarioLotesSimpleDTO;
import farmacias.AppOchoa.dto.inventariolotes.InventarioLotesUpdateDTO;
import farmacias.AppOchoa.model.*;
import farmacias.AppOchoa.repository.InventarioLotesRepository;
import farmacias.AppOchoa.repository.ProductoRepository;
import farmacias.AppOchoa.repository.SucursalRepository;
import farmacias.AppOchoa.exception.DuplicateResourceException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.services.InventarioLotesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class InventarioLotesServiceImpl implements InventarioLotesService {

    private final InventarioLotesRepository inventarioLotesRepository;
    private final ProductoRepository productoRepository;
    private final SucursalRepository sucursalRepository;

    public InventarioLotesServiceImpl(
            InventarioLotesRepository inventarioLotesRepository,
            ProductoRepository productoRepository,
            SucursalRepository sucursalRepository) {
        this.inventarioLotesRepository = inventarioLotesRepository;
        this.productoRepository = productoRepository;
        this.sucursalRepository = sucursalRepository;
    }

    @Override
    public InventarioLotesResponseDTO crear(Long farmaciaId, InventarioLotesCreateDTO dto) {
        // Sucursal 1:1 con la farmacia: se deriva del tenant, no se acepta del cliente
        Sucursal sucursal = buscarSucursal(farmaciaId);
        Producto producto = buscarProducto(farmaciaId, dto.getProductoId());

        // Unicidad por (sucursal, producto, numeroLote): mismo criterio que el
        // alta de lotes durante una compra (A2). Un mismo numero de lote puede
        // existir para productos distintos.
        if (inventarioLotesRepository.existsByLoteNumeroAndSucursal_SucursalIdAndProducto_ProductoId(
                dto.getNumeroLote(), sucursal.getSucursalId(), dto.getProductoId())) {
            throw new DuplicateResourceException("El número de lote " + dto.getNumeroLote() + " ya existe para este producto en esta sucursal");
        }

        InventarioLotes lote = InventarioLotes.builder()
                .loteNumero(dto.getNumeroLote())
                .loteCantidadInicial(dto.getCantidadInicial())
                .loteCantidadActual(dto.getCantidadInicial())
                .loteFechaVencimiento(dto.getFechaVencimiento())
                .lotePrecioCompra(dto.getPrecioCompra())
                .loteEstado(LoteEstado.disponible)
                .producto(producto)
                .sucursal(sucursal)
                .farmacia(sucursal.getFarmacia())
                .build();

        return InventarioLotesResponseDTO.fromEntity(inventarioLotesRepository.save(lote));
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioLotesResponseDTO buscarPorId(Long farmaciaId, Long id) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        InventarioLotes lote = inventarioLotesRepository.findByLoteIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado ID: " + id));
        return InventarioLotesResponseDTO.fromEntity(lote);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventarioLotesSimpleDTO> listarPorSucursalPaginado(Long farmaciaId, Long sucursalId, Pageable pageable) {
        // Sucursal 1:1 con la farmacia: se deriva del tenant, el sucursalId del cliente no se usa
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return inventarioLotesRepository.findBySucursal_SucursalId(sucursal.getSucursalId(), pageable)
                .map(InventarioLotesSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventarioLotesSimpleDTO> listarProximosAVencerPaginado(Long farmaciaId, LocalDate fechaLimite, Pageable pageable) {
        return inventarioLotesRepository.findByLoteFechaVencimientoLessThanEqual(fechaLimite, pageable)
                .map(InventarioLotesSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventarioLotesSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return inventarioLotesRepository.buscarPorTexto(sucursal.getSucursalId(), texto, pageable)
                .map(InventarioLotesSimpleDTO::fromEntity);
    }

    @Override
    public InventarioLotesResponseDTO actualizar(Long farmaciaId, Long id, InventarioLotesUpdateDTO dto) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        InventarioLotes lote = inventarioLotesRepository.findByLoteIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado ID: " + id));

        lote.setLoteCantidadActual(dto.getCantidadActual());
        lote.setLoteEstado(dto.getEstado());

        return InventarioLotesResponseDTO.fromEntity(inventarioLotesRepository.save(lote));
    }

    @Override
    public void eliminar(Long farmaciaId, Long id) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        InventarioLotes lote = inventarioLotesRepository.findByLoteIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado con ID: " + id));
        inventarioLotesRepository.delete(lote);
    }

    // Métodos auxiliares privados
    private Producto buscarProducto(Long farmaciaId, Long id) {
        return productoRepository.findById(id)
                .filter(p -> p.getFarmacia().getFarmaciaId().equals(farmaciaId))
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado en tu farmacia ID: " + id));
    }

    private Sucursal buscarSucursal(Long farmaciaId) {
        return sucursalRepository.findByFarmacia_FarmaciaId(farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada para tu farmacia"));
    }
}