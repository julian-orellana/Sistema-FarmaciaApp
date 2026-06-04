package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.producto.ProductoCreateDTO;
import farmacias.AppOchoa.dto.producto.ProductoResponseDTO;
import farmacias.AppOchoa.dto.producto.ProductoUpdateDTO;
import farmacias.AppOchoa.model.Categoria;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.Presentacion;
import farmacias.AppOchoa.model.Producto;
import farmacias.AppOchoa.repository.CategoriaRepository;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.repository.PresentacionRepository;
import farmacias.AppOchoa.repository.ProductoRepository;
import farmacias.AppOchoa.serviceimpl.ProductoServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private PresentacionRepository presentacionRepository;
    @Mock
    private FarmaciaRepository farmaciaRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    // TESTS PARA AGREGAR PRODUCTO

    @Test
    @DisplayName("Debería agregar producto correctamente cuando datos son válidos")
    void agregarProducto_Exito() {
        // ARRANGE
        Long farmaciaId = 1L;

        Farmacia farmaciaMock = new Farmacia();
        farmaciaMock.setFarmaciaId(farmaciaId);

        ProductoCreateDTO dto = new ProductoCreateDTO();
        dto.setNombre("Paracetamol 500mg");
        dto.setCodigoBarras("123456");
        dto.setCategoriaId(1L);
        dto.setPresentacionId(1L);
        dto.setPrecioCompra(BigDecimal.valueOf(10.0));
        dto.setPrecioVenta(BigDecimal.valueOf(15.0));
        dto.setIva(BigDecimal.ZERO);

        Categoria categoriaMock = new Categoria();
        categoriaMock.setCategoriaId(1L);
        categoriaMock.setCategoriaNombre("Farmacia General");

        Presentacion presentacionMock = new Presentacion();
        presentacionMock.setPresentacionId(1L);
        presentacionMock.setPresentacionNombre("Caja");

        Producto productoGuardado = Producto.builder()
                .productoId(1L)
                .productoNombre("Paracetamol 500mg")
                .farmacia(farmaciaMock)
                .categoria(categoriaMock)
                .presentacion(presentacionMock)
                .productoPrecioCompra(BigDecimal.valueOf(10.0))
                .productoPrecioVenta(BigDecimal.valueOf(15.0))
                .productoIva(BigDecimal.ZERO)
                .productoEstado(true)
                .build();

        when(farmaciaRepository.findById(farmaciaId)).thenReturn(Optional.of(farmaciaMock));
        when(productoRepository.existsByFarmacia_FarmaciaIdAndProductoNombre(farmaciaId, dto.getNombre())).thenReturn(false);
        when(productoRepository.existsByFarmacia_FarmaciaIdAndProductoCodigoBarras(farmaciaId, dto.getCodigoBarras())).thenReturn(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaMock));
        when(presentacionRepository.findById(1L)).thenReturn(Optional.of(presentacionMock));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);

        // ACT
        ProductoResponseDTO resultado = productoService.agregarProducto(farmaciaId, dto);

        // ASSERT
        assertNotNull(resultado);
        assertEquals("Paracetamol 500mg", resultado.getNombre());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción si el nombre del producto ya existe")
    void agregarProducto_FallaNombreDuplicado() {
        // ARRANGE
        Long farmaciaId = 1L;

        Farmacia farmaciaMock = new Farmacia();
        farmaciaMock.setFarmaciaId(farmaciaId);

        ProductoCreateDTO dto = new ProductoCreateDTO();
        dto.setNombre("Aspirina");

        when(farmaciaRepository.findById(farmaciaId)).thenReturn(Optional.of(farmaciaMock));
        when(productoRepository.existsByFarmacia_FarmaciaIdAndProductoNombre(farmaciaId, "Aspirina")).thenReturn(true);

        // ACT & ASSERT
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            productoService.agregarProducto(farmaciaId, dto);
        });

        assertTrue(excepcion.getMessage().contains("Ya existe un producto con el nombre:"));
        verify(productoRepository, never()).save(any());
    }

    // TESTS PARA ACTUALIZAR PRODUCTO

    @Test
    @DisplayName("Debería actualizar producto correctamente")
    void actualizarProducto_Exito() {
        // ARRANGE
        Long farmaciaId = 1L;
        Long idProducto = 1L;

        Farmacia farmaciaMock = new Farmacia();
        farmaciaMock.setFarmaciaId(farmaciaId);

        ProductoUpdateDTO updateDto = new ProductoUpdateDTO();
        updateDto.setNombre("Nuevo Nombre");
        updateDto.setCategoriaId(2L);
        updateDto.setPresentacionId(2L);
        updateDto.setCodigoBarras("99999");
        updateDto.setPrecioCompra(BigDecimal.valueOf(20.0));
        updateDto.setPrecioVenta(BigDecimal.valueOf(25.0));
        updateDto.setIva(BigDecimal.valueOf(0.12));

        Producto productoExistente = new Producto();
        productoExistente.setProductoId(idProducto);
        productoExistente.setProductoNombre("Nombre Viejo");
        productoExistente.setProductoCodigoBarras("11111");
        productoExistente.setProductoPrecioCompra(BigDecimal.valueOf(10.0));
        productoExistente.setFarmacia(farmaciaMock);

        Categoria catMock = new Categoria();
        catMock.setCategoriaId(2L);

        Presentacion presMock = new Presentacion();
        presMock.setPresentacionId(2L);

        when(productoRepository.findById(idProducto)).thenReturn(Optional.of(productoExistente));
        when(productoRepository.existsByFarmacia_FarmaciaIdAndProductoNombre(farmaciaId, "Nuevo Nombre")).thenReturn(false);
        when(productoRepository.existsByFarmacia_FarmaciaIdAndProductoCodigoBarras(farmaciaId, "99999")).thenReturn(false);
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(catMock));
        when(presentacionRepository.findById(anyLong())).thenReturn(Optional.of(presMock));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoExistente);

        // ACT
        productoService.actualizarProducto(farmaciaId, idProducto, updateDto);

        // ASSERT
        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());

        Producto productoCapturado = captor.getValue();
        assertEquals("Nuevo Nombre", productoCapturado.getProductoNombre());
        assertEquals("99999", productoCapturado.getProductoCodigoBarras());
        assertEquals(BigDecimal.valueOf(20.0), productoCapturado.getProductoPrecioCompra());
    }

    // TESTS DE OBTENCIÓN

    @Test
    @DisplayName("Debería lanzar excepción si buscamos un ID que no existe")
    void obtenerPorId_NoEncontrado() {
        // ARRANGE
        Long farmaciaId = 1L;
        Long idNoExistente = 99L;
        when(productoRepository.findById(idNoExistente)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(RuntimeException.class, () -> productoService.obtenerPorId(farmaciaId, idNoExistente));
    }

    // TESTS DE ELIMINACIÓN

    @Test
    @DisplayName("Eliminar producto debería cambiar estado a false (Borrado Lógico)")
    void eliminarProducto_CambiaEstado() {
        // ARRANGE
        Long farmaciaId = 1L;
        Long id = 5L;

        Farmacia farmaciaMock = new Farmacia();
        farmaciaMock.setFarmaciaId(farmaciaId);

        Producto productoMock = new Producto();
        productoMock.setProductoId(id);
        productoMock.setProductoEstado(true);
        productoMock.setFarmacia(farmaciaMock);

        when(productoRepository.findById(id)).thenReturn(Optional.of(productoMock));

        // ACT
        productoService.eliminarProducto(farmaciaId, id);

        // ASSERT
        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());

        assertFalse(captor.getValue().getProductoEstado(), "El estado debería haber cambiado a false");
    }
}