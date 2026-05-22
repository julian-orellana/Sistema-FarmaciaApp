package farmacias.AppOchoa.serviceImplTes;


import farmacias.AppOchoa.dto.farmacia.FarmaciaCreateDTO;
import farmacias.AppOchoa.dto.farmacia.FarmaciaResponseDTO;
import farmacias.AppOchoa.dto.farmacia.FarmaciaSimpleDTO;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.PlanTipo;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.serviceimpl.FarmaciaServiceImpl;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FarmaciaServiceImplTest {

    @Mock
    private FarmaciaRepository farmaciaRepository;
    @InjectMocks
    private FarmaciaServiceImpl farmaciaService;

    @Test
    @DisplayName("Deberia de crear una farmacia correctamente")
    void crearFarmaciaExitosa(){

        FarmaciaCreateDTO dto = new FarmaciaCreateDTO();
        dto.setFarmaciaNombre("Ochoa");
        dto.setFarmaciaNit("101363498");
        dto.setFarmaciaEmail("farmapp@ochoa.com");
        dto.setFarmaciaTelefono("55371140");
        dto.setPruebaHasta(LocalDate.of(2026, 5,30));
        dto.setPlanTipo(PlanTipo.basico);
        dto.setSuscripcionVigencia(LocalDate.of(2026, 3, 25));

        Farmacia farmacia = new Farmacia();
        farmacia.setFarmaciaId(1L);
        farmacia.setFarmaciaNombre("Ochoa");
        farmacia.setFarmaciaNit("101363498");
        farmacia.setFarmaciaEmail("farmapp@ochoa.com");
        farmacia.setFarmaciaTelefono("55371140");
        farmacia.setPruebaHasta(LocalDate.of(2026, 5,30));
        farmacia.setPlanTipo(PlanTipo.basico);
        farmacia.setSuscripcionVigencia(LocalDate.of(2026, 3, 25));

        when(farmaciaRepository.save(any(Farmacia.class))).thenReturn(farmacia);

        FarmaciaResponseDTO resultado  = farmaciaService.crear(dto);
        assertNotNull(resultado);
        assertEquals(dto.getFarmaciaNit(), resultado.getFarmaciaNit());

        ArgumentCaptor<Farmacia> captor = ArgumentCaptor.forClass(Farmacia.class);
        verify(farmaciaRepository).save(captor.capture());
        Farmacia farmacia1 = captor.getValue();
    }

    @Test
    @DisplayName("Deberia realizar una busqueda por texto exitosa")
    void crearBusqueda(){

        String texto = "Ochoa";
        Pageable pageable = PageRequest.of(0, 10);

        Farmacia farmacia = new Farmacia();
        farmacia.setFarmaciaNombre("Ochoa");
        Page<Farmacia> page = new PageImpl<>(List.of(farmacia));

        when(farmaciaRepository.buscarPorTexto(texto, pageable)).thenReturn(page);
        //ACT
        Page<FarmaciaSimpleDTO> resultado = farmaciaService.buscarPorTexto(texto, pageable);
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    @DisplayName("Deberia de lanzar una excepcion si se  intenta eliminar una farmacia")
    void eliminadoError(){
        assertThrows(UnsupportedOperationException.class, () -> {
            farmaciaService.eliminar(1L);
        });
    }


}
