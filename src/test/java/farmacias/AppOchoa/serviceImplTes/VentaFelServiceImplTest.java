package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.ventafel.VentaFelCreateDTO;
import farmacias.AppOchoa.dto.ventafel.VentaFelResponseDTO;
import farmacias.AppOchoa.dto.ventafel.VentaFelSimpleDTO;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.Venta;
import farmacias.AppOchoa.model.VentaFel;
import farmacias.AppOchoa.repository.FarmaciaRepository;
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
    private FarmaciaRepository farmaciaRepository;
    @Mock
    private VentaRepository ventaRepository;
    @InjectMocks
    private VentaFelServiceImpl ventaFelService;

    @Test
    @DisplayName("Deberia crear una venta correctamente")
    void crearVentaFel(){

        Long farmaciaId = 1L;

        VentaFelCreateDTO dto = new VentaFelCreateDTO();
        dto.setVentaId(1L);

        VentaFel ventaFel = new VentaFel();
        ventaFel.setFelId(1L);

        Farmacia farmacia = new Farmacia();
        farmacia.setFarmaciaId(1L);

        Venta venta = new Venta();
        venta.setVentaId(1L);

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
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

        Page<VentaFel> ventaFe = new PageImpl<>(List.of(ventaFel));
        when(ventaFelRepository.buscarPorTexto(texto, pageable)).thenReturn(ventaFe);
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
