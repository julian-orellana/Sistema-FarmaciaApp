package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.ventafel.VentaFelCreateDTO;
import farmacias.AppOchoa.dto.ventafel.VentaFelResponseDTO;
import farmacias.AppOchoa.dto.ventafel.VentaFelSimpleDTO;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.Sucursal;
import farmacias.AppOchoa.model.Venta;
import farmacias.AppOchoa.model.VentaFel;
import farmacias.AppOchoa.repository.SucursalRepository;
import farmacias.AppOchoa.repository.VentaFelRepository;
import farmacias.AppOchoa.repository.VentaRepository;
import farmacias.AppOchoa.serviceimpl.VentaFelServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VentaFelServiceImplTest {

    @Mock
    private VentaFelRepository ventaFelRepository;
    @Mock
    private SucursalRepository sucursalRepository;
    @Mock
    private VentaRepository ventaRepository;
    @InjectMocks
    private VentaFelServiceImpl ventaFelService;

    // Sucursal 1:1 con la farmacia; el service la resuelve desde farmaciaId
    private Sucursal sucursalDePrueba(Long farmaciaId, Long sucursalId) {
        Farmacia farmacia = new Farmacia();
        farmacia.setFarmaciaId(farmaciaId);
        Sucursal sucursal = new Sucursal();
        sucursal.setSucursalId(sucursalId);
        sucursal.setFarmacia(farmacia);
        return sucursal;
    }

    @Test
    @DisplayName("Deberia crear una venta correctamente")
    void crearVentaFel(){

        Long farmaciaId = 1L;

        VentaFelCreateDTO dto = new VentaFelCreateDTO();
        dto.setVentaId(1L);

        VentaFel ventaFel = new VentaFel();
        ventaFel.setFelId(1L);

        Venta venta = new Venta();
        venta.setVentaId(1L);

        Long sucursalId = 1L;
        when(sucursalRepository.findByFarmacia_FarmaciaId(farmaciaId)).thenReturn(Optional.of(sucursalDePrueba(farmaciaId, sucursalId)));
        when(ventaRepository.findByVentaIdAndSucursal_SucursalId(1L, sucursalId)).thenReturn(Optional.of(venta));
        when(ventaFelRepository.save(any(VentaFel.class))).thenReturn(ventaFel);

        VentaFelResponseDTO resultado = ventaFelService.crear(farmaciaId, dto);

        assertNotNull(resultado);
        assertEquals(ventaFel.getFelId(), resultado.getFelId());

        ArgumentCaptor<VentaFel> captor = ArgumentCaptor.forClass(VentaFel.class);
        verify(ventaFelRepository).save(captor.capture());
        VentaFel ventaFel1 = captor.getValue();
    }

    @Test
    @DisplayName("Deberia crear una busqueda correctamente")
    void crearBusqueda(){
        Long farmaciaId = 1L;
        String texto = "737849";
        Pageable pageable = PageRequest.of(0,10);

        VentaFel ventaFel = new VentaFel();
        ventaFel.setFelId(1L);

        Long sucursalId = 1L;
        Page<VentaFel> ventaFe = new PageImpl<>(List.of(ventaFel));
        when(sucursalRepository.findByFarmacia_FarmaciaId(farmaciaId)).thenReturn(Optional.of(sucursalDePrueba(farmaciaId, sucursalId)));
        when(ventaFelRepository.buscarPorTexto(sucursalId, texto, pageable)).thenReturn(ventaFe);

        Page<VentaFelSimpleDTO> resultado = ventaFelService.buscarPorTexto(farmaciaId, texto, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    @DisplayName("Deberia de lanzar una excepcion al eliminar una venta")
    void eliminadoError(){
        assertThrows(UnsupportedOperationException.class, () -> {
            ventaFelService.eliminar(1L, 1L);
        });
    }

}
