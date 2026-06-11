package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.inventariolotes.InventarioLotesCreateDTO;
import farmacias.AppOchoa.dto.inventariolotes.InventarioLotesResponseDTO;
import farmacias.AppOchoa.dto.inventariolotes.InventarioLotesSimpleDTO;
import farmacias.AppOchoa.dto.inventariolotes.InventarioLotesUpdateDTO;
import farmacias.AppOchoa.model.*;
import farmacias.AppOchoa.repository.FarmaciaRepository;
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
    private final FarmaciaRepository farmaciaRepository;

    public InventarioLotesServiceImpl(
            InventarioLotesRepository inventarioLotesRepository,
            ProductoRepository productoRepository,
            SucursalRepository sucursalRepository,
            FarmaciaRepository farmaciaRepository) {
        this.inventarioLotesRepository = inventarioLotesRepository;
        this.productoRepository = productoRepository;
        this.sucursalRepository = sucursalRepository;
        this.farmaciaRepository = farmaciaRepository;
    }

    @Override
    public InventarioLotesResponseDTO crear(Long farmaciaId, InventarioLotesCreateDTO dto) {
        Producto producto = buscarProducto(farmaciaId, dto.getProductoId());
        Sucursal sucursal = buscarSucursal(farmaciaId, dto.getSucursalId());

        // Unicidad por (sucursal, producto, numeroLote): mismo criterio que el
        // alta de lotes durante una compra (A2). Un mismo numero de lote puede
        // existir para productos distintos.
        if (inventarioLotesRepository.existsByLoteNumeroAndSucursal_SucursalIdAndProducto_ProductoId(
                dto.getNumeroLote(), dto.getSucursalId(), dto.getProductoId())) {
            throw new DuplicateResourceException("El número de lote " + dto.getNumeroLote() + " ya existe para este producto en esta sucursal");
        }
        Farmacia farmacia = farmaciaRepository.getReferenceById(farmaciaId);

        InventarioLotes lote = InventarioLotes.builder()
                .loteNumero(dto.getNumeroLote())
                .loteCantidadInicial(dto.getCantidadInicial())
                .loteCantidadActual(dto.getCantidadInicial())
                .loteFechaVencimiento(dto.getFechaVencimiento())
                .lotePrecioCompra(dto.getPrecioCompra())
                .loteEstado(LoteEstado.disponible)
                .producto(producto)
                .sucursal(sucursal)
                .farmacia(farmacia)
                .build();

        return InventarioLotesResponseDTO.fromEntity(inventarioLotesRepository.save(lote));
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioLotesResponseDTO buscarPorId(Long farmaciaId, Long id) {
        InventarioLotes lote = inventarioLotesRepository.findByLoteIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado ID: " + id));
        return InventarioLotesResponseDTO.fromEntity(lote);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventarioLotesSimpleDTO> listarPorSucursalPaginado(Long farmaciaId, Long sucursalId, Pageable pageable) {
        return inventarioLotesRepository.findBySucursal_SucursalIdAndFarmacia_FarmaciaId(sucursalId, farmaciaId, pageable)
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
        return inventarioLotesRepository.buscarPorTexto(farmaciaId, texto, pageable)
                .map(InventarioLotesSimpleDTO::fromEntity);
    }

    @Override
    public InventarioLotesResponseDTO actualizar(Long farmaciaId, Long id, InventarioLotesUpdateDTO dto) {
        InventarioLotes lote = inventarioLotesRepository.findByLoteIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado ID: " + id));

        lote.setLoteCantidadActual(dto.getCantidadActual());
        lote.setLoteEstado(dto.getEstado());

        return InventarioLotesResponseDTO.fromEntity(inventarioLotesRepository.save(lote));
    }

    @Override
    public void eliminar(Long farmaciaId, Long id) {
        InventarioLotes lote = inventarioLotesRepository.findByLoteIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado con ID: " + id));
        inventarioLotesRepository.delete(lote);
    }

    // Métodos auxiliares privados
    private Producto buscarProducto(Long farmaciaId, Long id) {
        return productoRepository.findById(id)
                .filter(p -> p.getFarmacia().getFarmaciaId().equals(farmaciaId))
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado en tu farmacia ID: " + id));
    }

    private Sucursal buscarSucursal(Long farmaciaId, Long id) {
        return sucursalRepository.findBySucursalIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada en tu farmacia ID: " + id));
    }
}