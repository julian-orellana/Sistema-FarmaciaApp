package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.producto.ProductoCreateDTO;
import farmacias.AppOchoa.dto.producto.ProductoResponseDTO;
import farmacias.AppOchoa.dto.producto.ProductoSimpleDTO;
import farmacias.AppOchoa.dto.producto.ProductoUpdateDTO;
import farmacias.AppOchoa.exception.BadRequestException;
import farmacias.AppOchoa.exception.DuplicateResourceException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.Categoria;
import farmacias.AppOchoa.model.Presentacion;
import farmacias.AppOchoa.model.Producto;
import farmacias.AppOchoa.model.Sucursal;
import farmacias.AppOchoa.repository.CategoriaRepository;
import farmacias.AppOchoa.repository.PresentacionRepository;
import farmacias.AppOchoa.repository.ProductoRepository;
import farmacias.AppOchoa.repository.SucursalRepository;
import farmacias.AppOchoa.services.ProductoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final PresentacionRepository presentacionRepository;
    private final SucursalRepository sucursalRepository;

    public ProductoServiceImpl(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository,
            PresentacionRepository presentacionRepository,
            SucursalRepository sucursalRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.presentacionRepository = presentacionRepository;
        this.sucursalRepository = sucursalRepository;
    }

    private Sucursal buscarSucursal(Long farmaciaId) {
        return sucursalRepository.findByFarmacia_FarmaciaId(farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada para tu farmacia"));
    }

    @Override
    public ProductoResponseDTO agregarProducto(Long farmaciaId, ProductoCreateDTO dto) {
        Sucursal sucursal = buscarSucursal(farmaciaId);

        if (productoRepository.existsBySucursal_SucursalIdAndProductoNombre(sucursal.getSucursalId(), dto.getNombre())) {
            throw new DuplicateResourceException("Ya existe un producto con el nombre: " + dto.getNombre());
        }

        if (dto.getCodigoBarras() != null && !dto.getCodigoBarras().isBlank() &&
                productoRepository.existsBySucursal_SucursalIdAndProductoCodigoBarras(sucursal.getSucursalId(), dto.getCodigoBarras())) {
            throw new DuplicateResourceException("El código de barras ya está registrado: " + dto.getCodigoBarras());
        }

        Categoria categoria = buscarCategoria(sucursal.getSucursalId(), dto.getCategoriaId());
        Presentacion presentacion = buscarPresentacion(sucursal.getSucursalId(), dto.getPresentacionId());

        Producto producto = Producto.builder()
                .productoNombre(dto.getNombre())
                .productoCodigoBarras(dto.getCodigoBarras())
                .productoPrecioCompra(dto.getPrecioCompra())
                .productoPrecioVenta(dto.getPrecioVenta())
                .productoIva(dto.getIva())
                .productoRequiereReceta(dto.getRequiereReceta() != null ? dto.getRequiereReceta() : false)
                .productoEstado(true)
                .categoria(categoria)
                .presentacion(presentacion)
                .farmacia(sucursal.getFarmacia())
                .sucursal(sucursal)
                .build();

        return ProductoResponseDTO.fromEntity(productoRepository.save(producto));
    }

    @Override
    public ProductoResponseDTO actualizarProducto(Long farmaciaId, Long id, ProductoUpdateDTO dto) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        if (!producto.getFarmacia().getFarmaciaId().equals(farmaciaId)) {
            throw new BadRequestException("No tienes permiso para modificar un producto de otra farmacia");
        }

        if (!producto.getProductoNombre().equalsIgnoreCase(dto.getNombre()) &&
                productoRepository.existsBySucursal_SucursalIdAndProductoNombre(sucursal.getSucursalId(), dto.getNombre())) {
            throw new DuplicateResourceException("Ya existe otro producto con el nombre: " + dto.getNombre());
        }

        if (dto.getCodigoBarras() != null &&
                !dto.getCodigoBarras().equals(producto.getProductoCodigoBarras()) &&
                productoRepository.existsBySucursal_SucursalIdAndProductoCodigoBarras(sucursal.getSucursalId(), dto.getCodigoBarras())) {
            throw new DuplicateResourceException("El código de barras ya pertenece a otro producto: " + dto.getCodigoBarras());
        }

        producto.setCategoria(buscarCategoria(sucursal.getSucursalId(), dto.getCategoriaId()));
        producto.setPresentacion(buscarPresentacion(sucursal.getSucursalId(), dto.getPresentacionId()));
        producto.setProductoNombre(dto.getNombre());
        producto.setProductoCodigoBarras(dto.getCodigoBarras());
        producto.setProductoPrecioCompra(dto.getPrecioCompra());
        producto.setProductoPrecioVenta(dto.getPrecioVenta());
        producto.setProductoIva(dto.getIva());

        if (dto.getEstado() != null) producto.setProductoEstado(dto.getEstado());
        if (dto.getRequiereReceta() != null) producto.setProductoRequiereReceta(dto.getRequiereReceta());

        return ProductoResponseDTO.fromEntity(productoRepository.save(producto));
    }

    @Override
    public void eliminarProducto(Long farmaciaId, Long id) {
        cambiarEstado(farmaciaId, id, false);
    }

    @Override
    public void cambiarEstado(Long farmaciaId, Long id, Boolean nuevoEstado) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        if (!producto.getFarmacia().getFarmaciaId().equals(farmaciaId)) {
            throw new BadRequestException("No tienes permiso para modificar un producto de otra farmacia");
        }

        producto.setProductoEstado(nuevoEstado);
        productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorCodigoBarras(Long farmaciaId, String codigo) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return productoRepository.findBySucursal_SucursalIdAndProductoCodigoBarras(sucursal.getSucursalId(), codigo)
                .map(ProductoResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con código de barras: " + codigo));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return productoRepository.buscarPorTexto(sucursal.getSucursalId(), texto, pageable)
                .map(ProductoSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(Long farmaciaId, Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        if (!producto.getFarmacia().getFarmaciaId().equals(farmaciaId)) {
            throw new BadRequestException("No tienes permiso para ver un producto de otra farmacia");
        }

        return ProductoResponseDTO.fromEntity(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoSimpleDTO> listarProductosActivos(Long farmaciaId, Pageable pageable) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return productoRepository.findBySucursal_SucursalIdAndProductoEstadoTrue(sucursal.getSucursalId(), pageable)
                .map(ProductoSimpleDTO::fromEntity);
    }

    private Categoria buscarCategoria(Long sucursalId, Long id) {
        if (id == null) return null;
        return categoriaRepository.findByCategoriaIdAndSucursal_SucursalId(id, sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada en tu farmacia con ID: " + id));
    }

    private Presentacion buscarPresentacion(Long sucursalId, Long id) {
        if (id == null) return null;
        return presentacionRepository.findByPresentacionIdAndSucursal_SucursalId(id, sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("Presentación no encontrada en tu farmacia con ID: " + id));
    }
}