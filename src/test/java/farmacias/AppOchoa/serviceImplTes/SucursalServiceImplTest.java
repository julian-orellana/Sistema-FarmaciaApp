package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.sucursal.SucursalCreateDTO;
import farmacias.AppOchoa.dto.sucursal.SucursalResponseDTO;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.Sucursal;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.repository.SucursalRepository;
import farmacias.AppOchoa.serviceimpl.SucursalServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SucursalServiceImplTest {

    @Mock
    private FarmaciaRepository farmaciaRepository;
    @Mock
    private SucursalRepository sucursalRepository;
    @InjectMocks
    private SucursalServiceImpl sucursalService;

    @Test
    @DisplayName("Debería de crear una sucursal correctamente")
    void crearSucursal(){

        Long farmaciaId = 1L;

        SucursalCreateDTO dto = new SucursalCreateDTO();
        dto.setNombre("Farmacia Valle");
        dto.setDireccion("17 Calle A");
        dto.setTelefono("77334455");
        dto.setSucursalNit("123456789");

        Sucursal sucursal = new Sucursal();
        sucursal.setSucursalNombre("Farmacia Valle");
        sucursal.setSucursalDireccion("17 Calle A");
        sucursal.setSucursalTelefono("77334455");
        sucursal.setSucursalNit("123456789");
        sucursal.setSucursalEstado(true);

        Farmacia farmacia = new Farmacia();
        farmacia.setFarmaciaId(1L);
        farmacia.setFarmaciaNombre("Orellana");

        when(sucursalRepository.save(any(Sucursal.class))).thenReturn(sucursal);
        when(farmaciaRepository.findById(1L)).thenReturn(Optional.of(farmacia));

        SucursalResponseDTO resultado = sucursalService.crear(1L, dto);

        assertNotNull(resultado);
        assertEquals(sucursal.getSucursalNit(), resultado.getSucursalNit());

        verify(sucursalRepository, times(1)).save(any(Sucursal.class));
        verify(farmaciaRepository, times(1)).findById(1L);

    }
}
