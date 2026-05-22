package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.cajasesiones.CajaSesionesCreateDTO;
import farmacias.AppOchoa.dto.cajasesiones.CajaSesionesResponseDTO;
import farmacias.AppOchoa.dto.cajasesiones.CajaSesionesSimpleDTO;
import farmacias.AppOchoa.model.Caja;
import farmacias.AppOchoa.model.CajaSesiones;
import farmacias.AppOchoa.model.Usuario;
import farmacias.AppOchoa.repository.CajaRepository;
import farmacias.AppOchoa.repository.CajaSesionesRepository;
import farmacias.AppOchoa.repository.UsuarioRepository;
import farmacias.AppOchoa.serviceimpl.CajaSesionesServiceImpl;
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
public class CajaSesionesImplTest {

    @Mock
    private CajaRepository cajaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CajaSesionesRepository cajaSesionesRepository;
    @InjectMocks
    private CajaSesionesServiceImpl cajaSesionesService;

    @Test
    @DisplayName("Deberia de crear un inicio de sesion en un caja exitosamente")
    void crearSesion(){

        Long farmaciaId = 1L;

        //ASSERT
        CajaSesionesCreateDTO dto = new CajaSesionesCreateDTO();
        dto.setUsuarioId(1L);
        dto.setCajaId(1L);
        dto.setSesionFondoInicial(new BigDecimal(500.00));

        Usuario usuario = new Usuario();
        usuario.setUsuarioId(1L);

        Caja caja = new Caja();
        caja.setCajaId(1L);

        CajaSesiones cajaSesiones = new CajaSesiones();
        caja.setCajaId(1L);
        cajaSesiones.setSesionId(1L);
        caja.setCajaNombre("Caja1");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(cajaRepository.findById(1L)).thenReturn(Optional.of(caja));
        when(cajaSesionesRepository.save(any(CajaSesiones.class))).thenReturn(cajaSesiones);

        CajaSesionesResponseDTO resultado = cajaSesionesService.crear(farmaciaId, dto);

        assertNotNull(resultado);
        assertEquals(cajaSesiones.getSesionId(), resultado.getSesionId());

        ArgumentCaptor<CajaSesiones> cajaSesionesArgumentCaptor = ArgumentCaptor.forClass(CajaSesiones.class);
        verify(cajaSesionesRepository).save(cajaSesionesArgumentCaptor.capture());
        CajaSesiones cajaSesiones1 = cajaSesionesArgumentCaptor.getValue();
    }

    @Test
    @DisplayName("Deberia de crear un inicio de sesion en un caja exitosamente")
    void crearSesionBuscarTexto(){

        Long farmaciaId = 1L;
        String texto = "Caja1";
        Pageable pageable = PageRequest.of(0, 10);

        // ARRANGE
        CajaSesiones cajaSesiones = new CajaSesiones();
        cajaSesiones.setSesionId(1L);

        Page<CajaSesiones> page = new PageImpl<>(List.of(cajaSesiones));

        when(cajaSesionesRepository.buscarPorTexto(texto, pageable)).thenReturn(page);

        // ACT
        Page<CajaSesionesSimpleDTO> resultado = cajaSesionesService.buscarPorTexto(farmaciaId, texto, pageable);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());

    }

    @Test
    @DisplayName("Deberia lanzar una excepcion si se elimina una sesion")
    void eliminadoError(){
        assertThrows(UnsupportedOperationException.class, () -> {
            cajaSesionesService.eliminar(1L,1L);
        });


    }



}
