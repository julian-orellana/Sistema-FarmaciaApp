package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.cajacorte.CajaCorteCreateDTO;
import farmacias.AppOchoa.dto.cajacorte.CajaCorteResponseDTO;
import farmacias.AppOchoa.dto.cajacorte.CajaCorteSimpleDTO;
import farmacias.AppOchoa.model.CajaCorte;
import farmacias.AppOchoa.model.CajaSesiones;
import farmacias.AppOchoa.model.Usuario;
import farmacias.AppOchoa.repository.CajaCortesRepository;
import farmacias.AppOchoa.repository.CajaSesionesRepository;
import farmacias.AppOchoa.repository.UsuarioRepository;
import farmacias.AppOchoa.repository.VentaPagoRepository;
import farmacias.AppOchoa.serviceimpl.CajaCorteServiceImpl;
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
public class CajaCorteServiceImplTest {

    @Mock
    private CajaCortesRepository cajaCortesRepository;
    @Mock
    private CajaSesionesRepository cajaSesionesRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private VentaPagoRepository ventaPagoRepository;
    @InjectMocks
    private CajaCorteServiceImpl cajaCorteService;

    @Test
    @DisplayName("Deberia crear un corte de caja correctamente")
    void crearCajaCortes(){

        Long farmaciaId = 1L;

        //ASSERT
        CajaCorteCreateDTO dto = new CajaCorteCreateDTO();
        dto.setSesionId(1L);
        dto.setUsuarioSupervisorId(1L);
        dto.setEfectivoFisicoContado(new BigDecimal(800.00));

        CajaSesiones cajaSesiones = new CajaSesiones();
        cajaSesiones.setSesionId(1L);

        Usuario usuario = new Usuario();
        usuario.setUsuarioId(1L);

        CajaCorte cajaCorte = new CajaCorte();
        cajaCorte.setCorteId(1L);
        cajaCorte.setCorteTotalEfectivo(new BigDecimal(800.00));

        when(cajaSesionesRepository.findById(1L)).thenReturn(Optional.of(cajaSesiones));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(ventaPagoRepository.sumarPorSesionYMetodo(any(), any())).thenReturn(BigDecimal.ZERO);
        when(ventaPagoRepository.sumarTotalPorSesion(any())).thenReturn(BigDecimal.ZERO);
        when(cajaCortesRepository.save(any(CajaCorte.class))).thenReturn(cajaCorte);

        CajaCorteResponseDTO resultado = cajaCorteService.crear(farmaciaId, dto);

        assertNotNull(resultado);
        assertEquals(dto.getEfectivoFisicoContado(), resultado.getCorteTotalEfectivo());

        ArgumentCaptor<CajaCorte> cajaCorteArgumentCaptor = ArgumentCaptor.forClass(CajaCorte.class);
        verify(cajaCortesRepository).save(cajaCorteArgumentCaptor.capture());
    }

    @Test
    @DisplayName("Deberia de buscar por Texto un corte de Caja")
    void buscarTexto(){

        Long farmaciaId = 1L;
        String texto = "Caja1";
        Pageable pageable = PageRequest.of(0, 10);

        //ARRANGE
        CajaCorte cajaCorte = new CajaCorte();
        cajaCorte.setCorteId(1L);

        Page<CajaCorte> page = new PageImpl<>(List.of(cajaCorte));
        when(cajaCortesRepository.buscarPorTexto(texto, pageable)).thenReturn(page);

        Page<CajaCorteSimpleDTO> resultado = cajaCorteService.buscarPorTexto(farmaciaId, texto, pageable);
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());

    }

    @Test
    @DisplayName("Deberia de lanzar una excepcion al eliminar")
    void eliminadoHistorico(){
        assertThrows(UnsupportedOperationException.class, () -> {
            cajaCorteService.eliminar(1L, 1L);
        });

    }



}
