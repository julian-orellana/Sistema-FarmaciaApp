package farmacias.AppOchoa.serviceImplTes;

import farmacias.AppOchoa.dto.categoria.CategoriaCreateDTO;
import farmacias.AppOchoa.dto.categoria.CategoriaResponseDTO;
import farmacias.AppOchoa.dto.categoria.CategoriaUpdateDTO;
import farmacias.AppOchoa.model.Categoria;
import farmacias.AppOchoa.repository.CategoriaRepository;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.serviceimpl.CategoriaServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceImplTest {
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private FarmaciaRepository farmaciaRepository;
    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    //TEST PARA CREAR CATEGORIA
    //ARRANGE
    @Test
    @DisplayName("Deberia de crear una categoria cuando los datos son validos")
    void crearCategoriaExitosa(){
        Long farmaciaId = 1L;
        CategoriaCreateDTO dto = new CategoriaCreateDTO();
        dto.setNombre("Vitaminas");
        Categoria categoria = Categoria.builder()
                .categoriaId(1L)
                .categoriaNombre("Vitaminas")
                .categoriaEstado(true)
                .build();

        when(categoriaRepository.existsByFarmacia_FarmaciaIdAndCategoriaNombre(any(), any())).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        CategoriaResponseDTO resultado = categoriaService.crear(farmaciaId, dto);

        assertNotNull(resultado);
        assertEquals("Vitaminas", resultado.getNombre());
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deberia lanzar una Excepcion si ya existe una Categoria con ese nombre")
    void crearCategoriaDuplicado(){
        Long farmaciaId = 1L;
        CategoriaCreateDTO dto = new CategoriaCreateDTO();
        dto.setNombre("Suplementos");

        when(categoriaRepository.existsByFarmacia_FarmaciaIdAndCategoriaNombre(any(), any())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> categoriaService.crear(farmaciaId, dto));
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }
    @Test
    @DisplayName("Deberia de lanzar una excepcion si buscamos un ID que no existe")
    void obtenerPorIdNoEncontrado(){
        Long farmaciaId = 1L;
        Long idNoExistente = 1L;
        when(categoriaRepository.findByCategoriaIdAndFarmacia_FarmaciaId(idNoExistente, farmaciaId)).thenReturn(Optional.empty());
        //ACT & ASSERT
        assertThrows(RuntimeException.class, () ->
                categoriaService.obtenerPorId(farmaciaId, idNoExistente));
    }
    @Test
    @DisplayName("Deberia de eliminar una categoria cambiandole de estado")
    void eliminarCategoria_BorradoLogico(){
        Long farmaciaId = 1L;
        Long id = 1L;
        Categoria categoria = new Categoria();
        categoria.setCategoriaId(id);
        categoria.setCategoriaEstado(true);

        when(categoriaRepository.findByCategoriaIdAndFarmacia_FarmaciaId(id, farmaciaId)).thenReturn(Optional.of(categoria));
        //ACT
        categoriaService.eliminar(farmaciaId, id);
        //ASSERT
        ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);
        verify(categoriaRepository).save(captor.capture());
        assertFalse(captor.getValue().getCategoriaEstado(), "El estado deberia de haber cambiado a false");
    }
    @Test
    @DisplayName("Deberia de actualizar una categoria correctamente con los datos validos")
    void actualizarCategoriaCorrectamente(){
        Long farmaciaId = 1L;
        Long id = 1L;
        CategoriaUpdateDTO dto = new CategoriaUpdateDTO();
        dto.setNombre("Bebes");

        Categoria categoriaRegistrada = Categoria.builder()
                .categoriaId(id)
                .categoriaNombre("Sueros")
                .categoriaEstado(true)
                .build();

        Categoria categoriaActualizada = Categoria.builder()
                .categoriaId(id)
                .categoriaNombre("Bebes")
                .categoriaEstado(true)
                .build();

        when(categoriaRepository.findByCategoriaIdAndFarmacia_FarmaciaId(id, farmaciaId)).thenReturn(Optional.of(categoriaRegistrada));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaActualizada);
        //ACT
        CategoriaResponseDTO resultado = categoriaService.actualizar(farmaciaId, id, dto);
        //ASSERT
        assertNotNull(resultado);
        assertEquals("Bebes", resultado.getNombre(), "El nombre deberia de haberse actualizado");
        verify(categoriaRepository).save(any(Categoria.class));
    }
    @Test
    @DisplayName("Deberia lanzar una excepcion al actualizar un ID que no existe")
    void falloActualizar(){
        Long farmaciaId = 1L;
        Long id = 1L;
        CategoriaUpdateDTO dto = new CategoriaUpdateDTO();
        dto.setNombre("Lactancia");

        when(categoriaRepository.findByCategoriaIdAndFarmacia_FarmaciaId(id, farmaciaId)).thenReturn(Optional.empty());
        //ASSERT
        assertThrows(RuntimeException.class,() ->{
            categoriaService.actualizar(farmaciaId, id, dto);
        });
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }


}
