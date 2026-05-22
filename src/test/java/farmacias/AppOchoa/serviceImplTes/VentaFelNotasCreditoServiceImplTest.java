package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.ventafelnotascredito.VentaFelNotasCreditoCreateDTO;
import farmacias.AppOchoa.dto.ventafelnotascredito.VentaFelNotasCreditoResponseDTO;
import farmacias.AppOchoa.dto.ventafelnotascredito.VentaFelNotasCreditoSimpleDTO;
import farmacias.AppOchoa.model.VentaFel;
import farmacias.AppOchoa.model.VentaFelNotasCredito;
import farmacias.AppOchoa.repository.VentaFelNotasCreditoRepository;
import farmacias.AppOchoa.repository.VentaFelRepository;
import farmacias.AppOchoa.serviceimpl.VentaFelNotasCreditoServiceImpl;
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
public class VentaFelNotasCreditoServiceImplTest {

    @Mock
    private VentaFelNotasCreditoRepository ventaFelNotasCreditoRepository;
    @Mock
    private VentaFelRepository ventaFelRepository;
    @InjectMocks
    private VentaFelNotasCreditoServiceImpl ventaFelNotasCreditoService;

    @Test
    @DisplayName("Deberia de crear una Nota de Credito correctamente")
    void crearNotaExitosa(){

        Long farmaciaId = 1L;

        VentaFelNotasCreditoCreateDTO dto = new VentaFelNotasCreditoCreateDTO();
        dto.setFelId(1L);
        dto.setNotaMotivo("Calidad mala");

        VentaFelNotasCredito ventaFelNotasCredito = new VentaFelNotasCredito();
        ventaFelNotasCredito.setNotaId(1L);
        ventaFelNotasCredito.setNotaMotivo("Calidad mala");
        ventaFelNotasCredito.setNotaNumeroAutorizacion("893403jskdk5555");

        VentaFel ventaFel = new VentaFel();
        ventaFel.setFelId(1L);

        when(ventaFelRepository.findById(1L)).thenReturn(Optional.of(ventaFel));
        when(ventaFelNotasCreditoRepository.save(any(VentaFelNotasCredito.class))).thenReturn(ventaFelNotasCredito);

        VentaFelNotasCreditoResponseDTO resultado = ventaFelNotasCreditoService.crear(farmaciaId, dto);

        assertNotNull(resultado);
        assertEquals(ventaFelNotasCredito.getNotaId(), resultado.getNotaId());

        ArgumentCaptor<VentaFelNotasCredito> captor = ArgumentCaptor.forClass(VentaFelNotasCredito.class);
        verify(ventaFelNotasCreditoRepository).save(captor.capture());
        VentaFelNotasCredito ventaFelNotasCredito1 = captor.getValue();
    }

    @Test
    @DisplayName("Deberia de buscar por Texto correctamente")
    void buscarNota(){

        Long farmaciaId = 1L;
        String texto = "Reclamo";
        Pageable pageable = PageRequest.of(0,10);

        VentaFelNotasCredito ventaFelNotasCredito = new VentaFelNotasCredito();
        ventaFelNotasCredito.setNotaId(1L);

        Page<VentaFelNotasCredito> page = new PageImpl<>(List.of(ventaFelNotasCredito));
        when(ventaFelNotasCreditoRepository.buscarPorTexto(texto, pageable)).thenReturn(page);
        Page<VentaFelNotasCreditoSimpleDTO> resultado = ventaFelNotasCreditoService.buscarPorTexto(
                farmaciaId, texto, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    @DisplayName("Deberia de lanzar excepcion al eliminar una Nota de Credito")
    void eliminadoError(){
        assertThrows(UnsupportedOperationException.class, () -> {
            ventaFelNotasCreditoService.eliminar(1L, 1L);
        });
    }


}
