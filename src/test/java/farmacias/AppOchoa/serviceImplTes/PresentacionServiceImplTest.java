package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.categoria.CategoriaUpdateDTO;
import farmacias.AppOchoa.dto.presentacion.PresentacionCreateDTO;
import farmacias.AppOchoa.dto.presentacion.PresentacionResponseDTO;
import farmacias.AppOchoa.dto.presentacion.PresentacionUpdateDTO;
import farmacias.AppOchoa.model.Categoria;
import farmacias.AppOchoa.model.Presentacion;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.repository.PresentacionRepository;
import farmacias.AppOchoa.serviceimpl.PresentacionServiceImpl;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSetType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PresentacionServiceImplTest {
    @Mock
    private PresentacionRepository presentacionRepository;
    @Mock
    private FarmaciaRepository farmaciaRepository;
    @InjectMocks
    private PresentacionServiceImpl presentacionService;

    //TEST PARA AGREGAR PRESENTACION
    @Test
    @DisplayName("Deberia de crear una presentacion correctamente cuando los datos son validos")
    void crearPresentacionExitosa(){
        //ARRANGE
        Long farmaciaId = 1L;
        PresentacionCreateDTO dto = new PresentacionCreateDTO();
        dto.setNombre("Caja de 12 Unidades");

        Presentacion presentacionGuardado = Presentacion.builder()
                .presentacionId(1L)
                .presentacionNombre("Caja de 12 Unidades")
                .presentacionEstado(true)
                .build();

        when(presentacionRepository.existsByFarmacia_FarmaciaIdAndPresentacionNombre(any(),anyString())).thenReturn(false);
        when(presentacionRepository.save(ArgumentMatchers.any(Presentacion.class))).thenReturn(presentacionGuardado);

        //ACT
        PresentacionResponseDTO resultado = presentacionService.crear(farmaciaId, dto);

        //ASSERT
        assertNotNull(resultado);
        assertEquals("Caja de 12 Unidades", resultado.getNombre());
        verify(presentacionRepository).save(ArgumentMatchers.any(Presentacion.class));
    }

    @Test
    @DisplayName("Deberia de lanzar una excepcion si ya existe una presentacion con ese nombre")
    void crearPresentacionFallaNombreDuplicado(){
        //ARRANGE

        Long farmaciaId = 1L;
        PresentacionCreateDTO dto = new PresentacionCreateDTO();
        dto.setNombre("Tabletas de 10 Unidades");

        Presentacion presentacionGuarda = Presentacion.builder()
                .presentacionId(1L)
                .presentacionNombre("Tabletas de 10 Unidades")
                .presentacionEstado(true)
                .build();

        when(presentacionRepository.existsByFarmacia_FarmaciaIdAndPresentacionNombre(any(),any())).thenReturn(true);

        //ASSERT & ACT
        assertThrows(RuntimeException.class, () ->{
            presentacionService.crear(farmaciaId, dto);
        });
        verify(presentacionRepository, never()).save(any(Presentacion.class));

    }

    @Test
    @DisplayName("Deberia lanzar una excepcion si buscamos un ID que no existe")
    void obtenerPorIdNoEncontrado(){

        Long farmaciaId = 1L;
        Long idNoExistente = 1l;
        when(presentacionRepository.findByPresentacionIdAndFarmacia_FarmaciaId(idNoExistente, farmaciaId)).thenReturn(Optional.empty());

        //ACT & ASSERT
        assertThrows(RuntimeException.class, () ->
            presentacionService.obtenerPorId(farmaciaId, idNoExistente));
        }


    @Test
    @DisplayName("Eliminar una presentacion deberia cambiar a estado false (Borrado Logico")
    void eliminarPresentacion_CambiarEstado(){
        //ARRANGE

        Long farmaciaId = 1L;
        Long id = 1L;
        Presentacion presentacionMock = new Presentacion();
        presentacionMock.setPresentacionId(id);
        presentacionMock.setPresentacionEstado(true);

        when(presentacionRepository.findByPresentacionIdAndFarmacia_FarmaciaId(id, farmaciaId)).thenReturn(Optional.of(presentacionMock));
        //ACT
        presentacionService.eliminar(farmaciaId, id);
        //ASSERT
        ArgumentCaptor<Presentacion> captor = ArgumentCaptor.forClass(Presentacion.class);
        verify(presentacionRepository).save(captor.capture());
        assertFalse(captor.getValue().getPresentacionEstado(), "El estado deberia de haber cambiado a false");
    }
    @Test
    @DisplayName("Deberia crear la actualizacion correctamente")
    void crearActualizacionCorrectamente(){

        Long farmaciaId = 1L;
        Long id = 1L;
        PresentacionUpdateDTO dto = new PresentacionUpdateDTO();
        dto.setNombre("Liquido 1000 ml.");

        Presentacion presentacionRegistrada = Presentacion.builder()
                .presentacionId(id)
                .presentacionNombre("Liquido 500 ml.")
                .presentacionEstado(true)
                .build();

        Presentacion presentacionActualizacion = Presentacion.builder()
                .presentacionId(id)
                .presentacionNombre("Liquido 1000 ml.")
                .presentacionEstado(true)
                .build();

        when(presentacionRepository.findByPresentacionIdAndFarmacia_FarmaciaId(id, farmaciaId)).thenReturn(Optional.of(presentacionRegistrada));
        when(presentacionRepository.save(any(Presentacion.class))).thenReturn(presentacionActualizacion);
        //ACT
        PresentacionResponseDTO resultado = presentacionService.actualizar(farmaciaId, id, dto);
        //ASSERT
        assertNotNull(resultado);
        assertEquals("Liquido 1000 ml.", resultado.getNombre(), "El nombre deberia de haberse actualizado");
        verify(presentacionRepository).save(any(Presentacion.class));
    }
    @Test
    @DisplayName("Deberia de lanzar una excepcion al actualizar un ID que no existe")
    void falloActualizacion(){

        Long farmaciaId = 1L;
        Long id = 1L;
        PresentacionUpdateDTO dto = new PresentacionUpdateDTO();
        dto.setNombre("Lactancia");

        when(presentacionRepository.findByPresentacionIdAndFarmacia_FarmaciaId(id, farmaciaId)).thenReturn(Optional.empty());
        //ASSERT
        assertThrows(RuntimeException.class, () ->{
            presentacionService.actualizar(farmaciaId, id, dto);
        });
        verify(presentacionRepository, never()).save(any(Presentacion.class));
    }


}



