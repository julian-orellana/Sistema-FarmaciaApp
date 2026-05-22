package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.suscripcionpago.SuscripcionPagoCreateDTO;
import farmacias.AppOchoa.dto.suscripcionpago.SuscripcionPagoResponseDTO;
import farmacias.AppOchoa.dto.suscripcionpago.SuscripcionPagoSimpleDTO;
import farmacias.AppOchoa.model.*;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.repository.SuscripcionPagoRepository;
import farmacias.AppOchoa.serviceimpl.SuscripcionPagoServiceImpl;
import farmacias.AppOchoa.services.SuscripcionPagoService;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SuscripcionPagoServiceImplTest {
    @Mock
    private SuscripcionPagoRepository suscripcionPagoRepository;
    @Mock
    private FarmaciaRepository farmaciaRepository;
    @InjectMocks
    private SuscripcionPagoServiceImpl suscripcionPagoService;

    @Test
    @DisplayName(("Deberia de crear una suscripcion correctamente"))
    void crearSuscripcion(){
        Long farmaciaId = 1L;

        SuscripcionPagoCreateDTO dto = new SuscripcionPagoCreateDTO();
        dto.setPagoMonto(new BigDecimal(600.00));
        dto.setPagoMetodo(PagoMetodo.efectivo);

        SuscripcionPago suscripcionPago = new SuscripcionPago();
        suscripcionPago.setPagoId(1L);
        suscripcionPago.setPagoMetodo(PagoMetodo.efectivo);
        suscripcionPago.setPagoEstado(PagoEstado.confirmado);
        suscripcionPago.setPagoPeriodoInicio(LocalDate.of(2026, 3, 27));
        suscripcionPago.setPagoPeriodoFin(LocalDate.of(2026, 4, 27));

        Farmacia farmacia = new Farmacia();
        farmacia.setFarmaciaId(1L);

        when(farmaciaRepository.findById(1L)).thenReturn(Optional.of(farmacia));
        when(suscripcionPagoRepository.save(any(SuscripcionPago.class))).thenReturn(suscripcionPago);

        SuscripcionPagoResponseDTO resultado = suscripcionPagoService.crear(farmaciaId, dto);

        assertNotNull(resultado);
        assertEquals(suscripcionPago.getPagoId(), resultado.getPagoId());

        ArgumentCaptor<SuscripcionPago> captor = ArgumentCaptor.forClass(SuscripcionPago.class);
        verify(suscripcionPagoRepository).save(captor.capture());
        SuscripcionPago suscripcionPago1 = captor.getValue();

    }

    @Test
    @DisplayName("Deberia de buscar una suscripcion por texto correctamente")
    void crearBusqueda(){

        Long farmaciaId = 1L;
        String texto = "Ochoa";
        Pageable pageable = PageRequest.of(0,10);

        SuscripcionPago suscripcionPago = new SuscripcionPago();
        suscripcionPago.setPagoPlan(PagoPlan.basico);
        suscripcionPago.setPagoMonto(new BigDecimal(800.00));
        suscripcionPago.setPagoEstado(PagoEstado.confirmado);
        suscripcionPago.setPagoPeriodoInicio(LocalDate.of(2026, 3, 27));
        suscripcionPago.setPagoPeriodoFin(LocalDate.of(2026, 4, 27));

        Page<SuscripcionPago> page = new PageImpl<>(List.of(suscripcionPago));
        when(suscripcionPagoRepository.buscarPorTexto(farmaciaId,texto, pageable)).thenReturn(page);
        Page<SuscripcionPagoSimpleDTO> resultado = suscripcionPagoService.buscarPorTexto(farmaciaId, texto, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    @DisplayName("Eliminar debería lanzar UnsupportedOperationException")
    void eliminar_DeberiaLanzarExcepcion() {
        assertThrows(UnsupportedOperationException.class, () -> {
            suscripcionPagoService.eliminar(1L, 1L);
        });
    }


}
