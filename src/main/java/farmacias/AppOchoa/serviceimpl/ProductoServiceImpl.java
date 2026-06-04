package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.producto.ProductoCreateDTO;
import farmacias.AppOchoa.dto.producto.ProductoResponseDTO;
import farmacias.AppOchoa.dto.producto.ProductoSimpleDTO;
import farmacias.AppOchoa.dto.producto.ProductoUpdateDTO;
import farmacias.AppOchoa.exception.BadRequestException;
import farmacias.AppOchoa.exception.DuplicateResourceException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.Categoria;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.Presentacion;
import farmacias.AppOchoa.model.Producto;
import farmacias.AppOchoa.repository.CategoriaRepository;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.repository.PresentacionRepository;
import farmacias.AppOchoa.repository.ProductoRepository;
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
    private final FarmaciaRepository farmaciaRepository;

    public ProductoServiceImpl(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository,
            PresentacionRepository presentacionRepository,
            FarmaciaRepository farmaciaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.presentacionRepository = presentacionRepository;
        this.farmaciaRepository = farmaciaRepository;
    }

    private Farmacia buscarFarmacia(Long farmaciaId) {
        return farmaciaRepository.findById(farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmacia no encontrada con ID: " + farmaciaId));
    }

    @Override
    public ProductoResponseDTO agregarProducto(Long farmaciaId, ProductoCreateDTO dto) {
        Farmacia farmacia = buscarFarmacia(farmaciaId);

        if (productoRepository.existsByFarmacia_FarmaciaIdAndProductoNombre(farmaciaId, dto.getNombre())) {
            throw new DuplicateResourceException("Ya existe un producto con el nombre: " + dto.getNombre());
        }

        if (dto.getCodigoBarras() != null && !dto.getCodigoBarras().isBlank() &&
                productoRepository.existsByFarmacia_FarmaciaIdAndProductoCodigoBarras(farmaciaId, dto.getCodigoBarras())) {
            throw new DuplicateResourceException("El código de barras ya está registrado: " + dto.getCodigoBarras());
        }

        Categoria categoria = buscarCategoria(dto.getCategoriaId());
        Presentacion presentacion = buscarPresentacion(dto.getPresentacionId());

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
                .farmacia(farmacia)
                .build();

        return ProductoResponseDTO.fromEntity(productoRepository.save(producto));
    }

    @Override
    public ProductoResponseDTO actualizarProducto(Long farmaciaId, Long id, ProductoUpdateDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        if (!producto.getFarmacia().getFarmaciaId().equals(farmaciaId)) {
            throw new BadRequestException("No tienes permiso para modificar un producto de otra farmacia");
        }

        if (!producto.getProductoNombre().equalsIgnoreCase(dto.getNombre()) &&
                productoRepository.existsByFarmacia_FarmaciaIdAndProductoNombre(farmaciaId, dto.getNombre())) {
            throw new DuplicateResourceException("Ya existe otro producto con el nombre: " + dto.getNombre());
        }

        if (dto.getCodigoBarras() != null &&
                !dto.getCodigoBarras().equals(producto.getProductoCodigoBarras()) &&
                productoRepository.existsByFarmacia_FarmaciaIdAndProductoCodigoBarras(farmaciaId, dto.getCodigoBarras())) {
            throw new DuplicateResourceException("El código de barras ya pertenece a otro producto: " + dto.getCodigoBarras());
        }

        producto.setCategoria(buscarCategoria(dto.getCategoriaId()));
        producto.setPresentacion(buscarPresentacion(dto.getPresentacionId()));
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
        return productoRepository.findByFarmacia_FarmaciaIdAndProductoCodigoBarras(farmaciaId, codigo)
                .map(ProductoResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con código de barras: " + codigo));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable) {
        return productoRepository.buscarPorTexto(texto, pageable)
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
        return productoRepository.findByFarmacia_FarmaciaIdAndProductoEstadoTrue(farmaciaId, pageable)
                .map(ProductoSimpleDTO::fromEntity);
    }

    private Categoria buscarCategoria(Long id) {
        if (id == null) return null;
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
    }

    private Presentacion buscarPresentacion(Long id) {
        if (id == null) return null;
        return presentacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presentación no encontrada con ID: " + id));
    }
}