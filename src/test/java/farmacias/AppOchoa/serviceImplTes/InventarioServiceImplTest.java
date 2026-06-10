package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.inventario.InventarioCreateDTO;
import farmacias.AppOchoa.dto.inventario.InventarioResponseDTO;
import farmacias.AppOchoa.dto.inventario.InventarioUpdateDTO;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.Inventario;
import farmacias.AppOchoa.model.Producto;
import farmacias.AppOchoa.model.Sucursal;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.repository.InventarioRepository;
import farmacias.AppOchoa.repository.ProductoRepository;
import farmacias.AppOchoa.repository.SucursalRepository;
import farmacias.AppOchoa.serviceimpl.InventarioServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventarioServiceImplTest {
    @Mock
    private InventarioRepository inventarioRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private SucursalRepository sucursalRepository;
    @Mock
    private FarmaciaRepository farmaciaRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;
    @Test
    @DisplayName("Deberia registrar un Item correctamente en el inventario")
    void crearRegistroExitoso(){
        Long farmaciaId = 1L;
        InventarioCreateDTO dto = new InventarioCreateDTO();
        dto.setSucursalId(1L);
        dto.setProductoId(1L);
        dto.setCantidadActual(80);
        dto.setCantidadMinima(12);

        Sucursal sucursal = new Sucursal();
        sucursal.setSucursalId(1L);
        sucursal.setSucursalNombre("Farmacia Central Ochoa");

        Producto producto = new Producto();
        producto.setProductoId(1L);
        producto.setProductoNombre("Acetaminofen Bayer");


        Inventario inventario = Inventario.builder()
                .inventarioId(1L)
                .inventarioCantidadActual(80)
                .inventarioCantidadMinima(12)
                .producto(producto)
                .sucursal(sucursal)
                .build();

        Farmacia farmacia = new Farmacia();
        farmacia.setFarmaciaId(farmaciaId);
        producto.setFarmacia(farmacia);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(sucursalRepository.findBySucursalIdAndFarmacia_FarmaciaId(1L, farmaciaId)).thenReturn(Optional.of(sucursal));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);
        //ACT
        InventarioResponseDTO resultado = inventarioService.crear(farmaciaId, dto);
        //ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getInventarioId());
        verify(inventarioRepository).save(any(Inventario.class));

    }
    @Test
    @DisplayName("Deberia de lanzar una excepcion al registrar un producto ya existente")
    void falloRegistro(){

        Long farmaciaId = 1L;
        InventarioCreateDTO dto =  new InventarioCreateDTO();
        dto.setProductoId(1L);
        dto.setProductoId(1L);
        dto.setSucursalId(1L);
        dto.setCantidadActual(60);
        dto.setCantidadMinima(12);
        when(inventarioRepository.existsByProducto_ProductoIdAndSucursal_SucursalId(1L, 1L)).thenReturn(true);
        //ACT & ASSERT
        RuntimeException excepcion = assertThrows(RuntimeException.class, () ->{
            inventarioService.crear(farmaciaId, dto);
        });
        assertTrue(excepcion.getMessage().contains("Ya existe un registro de inventario para este producto en la sucursal seleccionada"));
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }
    @Test
    @DisplayName("Deberia de Actualizar un registro correctamente")
    void actualizarRegistro(){

        Long farmaciaId = 1L;
        Long id = 1L;
        InventarioUpdateDTO dto = new InventarioUpdateDTO();
        dto.setCantidadActual(60);
        dto.setCantidadMinima(12);
        Producto producto = new Producto(); producto.setProductoId(1L);
        Sucursal sucursal = new Sucursal(); sucursal.setSucursalId(1L);

        Inventario inventarioExistente = Inventario.builder()
                .inventarioId(id)
                .producto(producto)
                .sucursal(sucursal)
                .inventarioCantidadActual(50)
                .build();

        Inventario inventarioActualizado = Inventario.builder()
                .inventarioId(id)
                .producto(producto)
                .sucursal(sucursal)
                .inventarioCantidadActual(60)
                .build();

        when(inventarioRepository.findByInventarioIdAndFarmacia_FarmaciaId(id, farmaciaId)).thenReturn(Optional.of(inventarioExistente));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventarioActualizado);

        //ACT
        InventarioResponseDTO resultado = inventarioService.actualizar(farmaciaId, id, dto);

        //ASSERT
        assertEquals(60, resultado.getCantidadActual());
        verify(inventarioRepository).save(any(Inventario.class));
    }
    @Test
    @DisplayName("Deberia de lanzar una excepcion si buscamos un ID no existente")
    void buscarRegistroFallo(){

        Long farmaciaId = 1L;
        Long id = 1L;
        when(inventarioRepository.findByInventarioIdAndFarmacia_FarmaciaId(id, farmaciaId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, ()->{
            inventarioService.listaPorId(farmaciaId, id);
        });
    }



}
