package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.cajacorte.CajaCorteCreateDTO;
import farmacias.AppOchoa.dto.cajacorte.CajaCorteResponseDTO;
import farmacias.AppOchoa.dto.cajacorte.CajaCorteSimpleDTO;
import farmacias.AppOchoa.model.CajaCorte;
import farmacias.AppOchoa.model.CajaSesiones;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.Sucursal;
import farmacias.AppOchoa.model.Usuario;
import farmacias.AppOchoa.repository.*;
import farmacias.AppOchoa.serviceimpl.CajaCorteServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    @Mock
    private SucursalRepository sucursalRepository;
    @InjectMocks
    private CajaCorteServiceImpl cajaCorteService;

    @AfterEach
    void limpiarContextoSeguridad() {
        SecurityContextHolder.clearContext();
    }

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
    @DisplayName("Deberia crear un corte de caja correctamente")
    void crearCajaCortes(){

        Long farmaciaId = 1L;


        //ASSERT
        CajaCorteCreateDTO dto = new CajaCorteCreateDTO();
        dto.setSesionId(1L);
        dto.setEfectivoFisicoContado(new BigDecimal(800.00));

        CajaSesiones cajaSesiones = new CajaSesiones();
        cajaSesiones.setSesionId(1L);
        Usuario usuario = new Usuario();
        usuario.setUsuarioId(1L);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null));

        CajaCorte cajaCorte = new CajaCorte();
        cajaCorte.setCorteId(1L);
        cajaCorte.setCorteTotalEfectivo(new BigDecimal(800.00));

        when(sucursalRepository.findByFarmacia_FarmaciaId(farmaciaId)).thenReturn(Optional.of(sucursalDePrueba(farmaciaId, 1L)));
        when(cajaSesionesRepository.findBySesionIdAndSucursal_SucursalId(any(), eq(1L))).thenReturn(Optional.of(cajaSesiones));
        when(usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(any(), eq(1L))).thenReturn(Optional.of(usuario));
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
    void buscarTexto() {
        Long farmaciaId = 1L;
        String texto = "Caja1";
        Pageable pageable = PageRequest.of(0, 10);

        //ARRANGE
        CajaCorte cajaCorte = new CajaCorte();
        cajaCorte.setCorteId(1L);

        Long sucursalId = 1L;
        Page<CajaCorte> page = new PageImpl<>(List.of(cajaCorte));
        when(sucursalRepository.findByFarmacia_FarmaciaId(farmaciaId)).thenReturn(Optional.of(sucursalDePrueba(farmaciaId, sucursalId)));
        when(cajaCortesRepository.buscarPorTexto(sucursalId, texto, pageable)).thenReturn(page);

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
