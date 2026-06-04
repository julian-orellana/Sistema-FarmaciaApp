package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.inventario.InventarioCreateDTO;
import farmacias.AppOchoa.dto.inventario.InventarioResponseDTO;
import farmacias.AppOchoa.dto.inventario.InventarioSimpleDTO;
import farmacias.AppOchoa.dto.inventario.InventarioUpdateDTO;
import farmacias.AppOchoa.model.*;
import farmacias.AppOchoa.repository.InventarioRepository;
import farmacias.AppOchoa.repository.ProductoRepository;
import farmacias.AppOchoa.repository.SucursalRepository;
import farmacias.AppOchoa.exception.BadRequestException;
import farmacias.AppOchoa.exception.DuplicateResourceException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.services.InventarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;
    private final SucursalRepository sucursalRepository;

    public InventarioServiceImpl(
            InventarioRepository inventarioRepository,
            ProductoRepository productoRepository,
            SucursalRepository sucursalRepository) {
        this.inventarioRepository = inventarioRepository;
        this.productoRepository = productoRepository;
        this.sucursalRepository = sucursalRepository;
    }

    @Override
    public InventarioResponseDTO crear(Long farmaciaId, InventarioCreateDTO dto) {
        if (inventarioRepository.existsByProducto_ProductoIdAndSucursal_SucursalId(
                dto.getProductoId(), dto.getSucursalId())) {
            throw new DuplicateResourceException("Ya existe un registro de inventario para este producto en la sucursal seleccionada");
        }

        Producto producto = buscarProducto(farmaciaId, dto.getProductoId());
        Sucursal sucursal = buscarSucursal(farmaciaId, dto.getSucursalId());

        Inventario inventario = Inventario.builder()
                .inventarioCantidadActual(dto.getCantidadActual())
                .inventarioCantidadMinima(dto.getCantidadMinima())
                .producto(producto)
                .sucursal(sucursal)
                .build();

        return InventarioResponseDTO.fromEntity(inventarioRepository.save(inventario));
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioResponseDTO listaPorId(Long farmaciaId, Long id) {
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado ID: " + id));
        return InventarioResponseDTO.fromEntity(inventario);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventarioSimpleDTO> listarTodosPaginado(Long farmaciaId, Pageable pageable) {
        return inventarioRepository.findAll(pageable)
                .map(InventarioSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventarioSimpleDTO> listarActivosPaginado(Long farmaciaId, Pageable pageable) {
        return inventarioRepository.findActivosPaginado(pageable)
                .map(InventarioSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventarioSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable){
        return inventarioRepository.buscarPorTexto(texto, pageable)
                .map(InventarioSimpleDTO::fromEntity);
    }

    @Override
    public InventarioResponseDTO actualizar(Long farmaciaId, Long id, InventarioUpdateDTO dto) {
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado ID: " + id));

        inventario.setInventarioCantidadActual(dto.getCantidadActual());
        inventario.setInventarioCantidadMinima(dto.getCantidadMinima());

        return InventarioResponseDTO.fromEntity(inventarioRepository.save(inventario));
    }


    @Override
    public void eliminar(Long farmaciaId, Long id){
        if(!inventarioRepository.existsById(id)){
            throw new ResourceNotFoundException("Inventario no encontrado con ID: " + id);
        }
        inventarioRepository.deleteById(id);
    }

    // Métodos auxiliares privados
    private Producto buscarProducto(Long farmaciaId, Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado ID: " + id));
    }

    private Sucursal buscarSucursal(Long farmaciaId, Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada ID: " + id));
    }
}