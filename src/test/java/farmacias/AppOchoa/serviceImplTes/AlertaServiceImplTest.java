package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.alerta.AlertaCreateDTO;
import farmacias.AppOchoa.dto.alerta.AlertaResponseDTO;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.Alerta;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.InventarioLotes;
import farmacias.AppOchoa.model.Producto;
import farmacias.AppOchoa.model.Sucursal;
import farmacias.AppOchoa.repository.*;
import farmacias.AppOchoa.serviceimpl.AlertaServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaServiceImplTest {

    @Mock
    private AlertaRepository alertaRepository;
    @Mock
    private SucursalRepository sucursalRepository;
    @Mock
    private InventarioLotesRepository inventarioLotesRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private FarmaciaRepository farmaciaRepository;

    @InjectMocks
    private AlertaServiceImpl alertaService;

    @Test
    @DisplayName("Deberia crear una alerta correcta con los datos ingresados")
    void crearAlertaExitosa() {

        Long farmaciaId = 1L;

        // DTO
        AlertaCreateDTO dto = new AlertaCreateDTO();
        dto.setLoteId(1L);
        dto.setMensaje("El stock de Suerox sabor fresa, se esta agotando");
        dto.setSucursalId(1L);
        dto.setProductoId(1L);

        // Mock entidades relacionadas
        Farmacia farmacia = new Farmacia();
        farmacia.setFarmaciaId(farmaciaId);

        Sucursal sucursal = new Sucursal();
        sucursal.setSucursalId(1L);

        InventarioLotes inventarioLotes = new InventarioLotes();
        inventarioLotes.setLoteId(1L);

        Producto producto = new Producto();
        producto.setProductoId(1L);
        producto.setFarmacia(farmacia);

        // Resultado simulado de save
        Alerta alertaGuardada = new Alerta();
        alertaGuardada.setAlertaId(1L);
        alertaGuardada.setAlertaMensaje(dto.getMensaje());

        // Mocks
        when(sucursalRepository.findBySucursalIdAndFarmacia_FarmaciaId(1L, farmaciaId)).thenReturn(Optional.of(sucursal));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(inventarioLotesRepository.findByLoteIdAndFarmacia_FarmaciaId(1L, farmaciaId)).thenReturn(Optional.of(inventarioLotes));
        when(alertaRepository.save(any(Alerta.class))).thenReturn(alertaGuardada);

        // ACT
        AlertaResponseDTO resultado = alertaService.crear(farmaciaId, dto);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(dto.getMensaje(), resultado.getMensaje());

        // Validar qué se guardó realmente
        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository).save(captor.capture());

        Alerta alertaEnviada = captor.getValue();
        assertEquals(dto.getMensaje(), alertaEnviada.getAlertaMensaje());
        assertEquals(producto, alertaEnviada.getProducto());
        assertEquals(sucursal, alertaEnviada.getSucursal());
        assertEquals(inventarioLotes, alertaEnviada.getLote());
    }

    @Test
    @DisplayName("Deberia de eliminar una alerta correctamente")
    void eliminarAlertaExitosa() {
        Long farmaciaId = 1L;
        Long id = 1L;

        Alerta alerta = new Alerta();
        alerta.setAlertaId(id);
        when(alertaRepository.findByAlertaIdAndFarmacia_FarmaciaId(id, farmaciaId)).thenReturn(Optional.of(alerta));

        alertaService.eliminar(farmaciaId, id);

        verify(alertaRepository).delete(alerta);
    }

    @Test
    @DisplayName("Deberia de lanzar una excepcion al eliminar una alerta por ID no encontrado")
    void alertaEliminarFalloId() {
        Long farmaciaId = 1L;
        Long id = 1L;

        when(alertaRepository.findByAlertaIdAndFarmacia_FarmaciaId(id, farmaciaId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            alertaService.eliminar(farmaciaId, id);
        });
    }
}