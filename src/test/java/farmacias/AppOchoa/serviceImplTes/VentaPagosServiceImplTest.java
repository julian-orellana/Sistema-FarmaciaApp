package farmacias.AppOchoa.serviceImplTes;


import farmacias.AppOchoa.dto.ventapago.VentaPagoCreateDTO;
import farmacias.AppOchoa.dto.ventapago.VentaPagoResponseDTO;
import farmacias.AppOchoa.dto.ventapago.VentaPagoSimpleDTO;
import farmacias.AppOchoa.model.CajaSesiones;
import farmacias.AppOchoa.model.MetodoPagoEstado;
import farmacias.AppOchoa.model.Venta;
import farmacias.AppOchoa.model.VentaPago;
import farmacias.AppOchoa.repository.CajaSesionesRepository;
import farmacias.AppOchoa.repository.VentaPagoRepository;
import farmacias.AppOchoa.repository.VentaRepository;
import farmacias.AppOchoa.serviceimpl.VentaPagosServiceImpl;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VentaPagosServiceImplTest {

    @Mock
    private VentaPagoRepository ventaPagoRepository;
    @Mock
    private VentaRepository ventaRepository;
    @Mock
    private CajaSesionesRepository cajaSesionesRepository;
    @InjectMocks
    private VentaPagosServiceImpl ventaPagosService;

    @Test
    @DisplayName("Deberia crear una ventan con pago")
    void crearVenta(){

        Long farmaciaId = 1L;

        VentaPagoCreateDTO dto = new VentaPagoCreateDTO();
        dto.setVentaId(1L);
        dto.setCajaSesionId(1L);
        dto.setMetodoPago(MetodoPagoEstado.efectivo);

        VentaPago ventaPago = new VentaPago();
        ventaPago.setPagoId(1L);
        ventaPago.setMetodoPago(MetodoPagoEstado.efectivo);
        ventaPago.setMontoRecibido(new BigDecimal(100.00));
        ventaPago.setMontoVuelto(new BigDecimal(55.15));

        CajaSesiones cajaSesiones = new CajaSesiones();
        cajaSesiones.setSesionId(1L);

        Venta venta = new Venta();
        venta.setVentaId(1L);

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(cajaSesionesRepository.findById(1L)).thenReturn(Optional.of(cajaSesiones));
        when(ventaPagoRepository.save(any(VentaPago.class))).thenReturn(ventaPago);

        VentaPagoResponseDTO resultado = ventaPagosService.crear(farmaciaId, dto);

        assertNotNull(resultado);
        assertEquals(ventaPago.getPagoId(), resultado.getPagoId());

        ArgumentCaptor<VentaPago> captor = ArgumentCaptor.forClass(VentaPago.class);
        verify(ventaPagoRepository).save(captor.capture());
        VentaPago ventaPago1 = captor.getValue();
    }

    @Test
    @DisplayName("Deberia de crear una busqueda exitosamente")
    void crearBusqueda(){

        Long farmaciaId = 1L;
        String texto = "CajaSesion";
        Pageable pageable = PageRequest.of(0,10);

        VentaPago ventaPago = new VentaPago();
        ventaPago.setPagoId(1L);
        ventaPago.setMetodoPago(MetodoPagoEstado.efectivo);
        ventaPago.setMontoRecibido(new BigDecimal(100.00));
        ventaPago.setMontoVuelto(new BigDecimal(55.15));

        Venta venta = new Venta();
        venta.setVentaId(1L);

        CajaSesiones cajaSesiones = new CajaSesiones();
        cajaSesiones.setSesionId(1L);

        Page<VentaPago> page = new PageImpl<>(List.of(ventaPago));
        when(ventaPagoRepository.buscarPorTexto(texto, pageable)).thenReturn(page);
        Page<VentaPagoSimpleDTO> resultado = ventaPagosService.buscarPorTexto(farmaciaId, texto, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    @DisplayName("Deberia de lanzar una excepcion al eliminar una Venta Pago")
    void eliminadoError(){
        assertThrows(UnsupportedOperationException.class, ()-> {
            ventaPagosService.eliminar(1L, 1L);
        });
    }
}
