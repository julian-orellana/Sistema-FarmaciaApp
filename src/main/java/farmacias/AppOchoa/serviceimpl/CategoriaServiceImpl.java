package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.categoria.CategoriaCreateDTO;
import farmacias.AppOchoa.dto.categoria.CategoriaResponseDTO;
import farmacias.AppOchoa.dto.categoria.CategoriaSimpleDTO;
import farmacias.AppOchoa.dto.categoria.CategoriaUpdateDTO;
import farmacias.AppOchoa.exception.DuplicateResourceException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.Categoria;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.repository.CategoriaRepository;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.services.CategoriaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final FarmaciaRepository farmaciaRepository;

    public CategoriaServiceImpl(
            CategoriaRepository categoriaRepository,
            FarmaciaRepository farmaciaRepository){
        this.categoriaRepository = categoriaRepository;
        this.farmaciaRepository = farmaciaRepository;
    }
    @Override
    public CategoriaResponseDTO crear(Long farmaciaId, CategoriaCreateDTO dto){
        if(categoriaRepository.existsByFarmacia_FarmaciaIdAndCategoriaNombre(farmaciaId, dto.getNombre())){
            throw new DuplicateResourceException("Ya existe una categoría con ese nombre: " + dto.getNombre());
        }
        Farmacia farmacia = farmaciaRepository.getReferenceById(farmaciaId);

        Categoria categoria = Categoria.builder()
                .categoriaNombre(dto.getNombre())
                .categoriaEstado(true)
                .farmacia(farmacia)
                .build();

        Categoria guardada = categoriaRepository.save(categoria);
        return CategoriaResponseDTO.fromEntity(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDTO obtenerPorId(Long farmaciaId, Long id){
        Categoria categoria = categoriaRepository.findByCategoriaIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        return CategoriaResponseDTO.fromEntity(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoriaSimpleDTO> listarTodasPaginadas(Long farmaciaId, Pageable pageable) {
        return categoriaRepository.findByFarmacia_FarmaciaId(farmaciaId, pageable)
                .map(CategoriaSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoriaSimpleDTO> listarActivasPaginadas(Long farmaciaId, Pageable pageable) {
        return categoriaRepository.findByFarmacia_FarmaciaIdAndCategoriaEstadoTrue(farmaciaId, pageable)
                .map(CategoriaSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoriaSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable){
        return categoriaRepository.buscarPorTexto(texto, pageable)
                .map(CategoriaSimpleDTO::fromEntity);
    }



    @Override
    public CategoriaResponseDTO actualizar(Long farmaciaId, Long id, CategoriaUpdateDTO dto){
        Categoria categoria = categoriaRepository.findByCategoriaIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(()-> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        if(!categoria.getCategoriaNombre().equals(dto.getNombre())){
            if(categoriaRepository.existsByFarmacia_FarmaciaIdAndCategoriaNombre(farmaciaId, dto.getNombre())){
                throw new DuplicateResourceException("Ya existe otra categoría con el nombre: " + dto.getNombre());
            }
        }

        categoria.setCategoriaNombre(dto.getNombre());
        categoria.setCategoriaEstado(dto.getEstado());

        Categoria actualizada = categoriaRepository.save(categoria);
        return CategoriaResponseDTO.fromEntity(actualizada);
    }

    @Override
    public void cambiarEstado(Long farmaciaId, Long id, Boolean estado){
        Categoria categoria = categoriaRepository.findByCategoriaIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        categoria.setCategoriaEstado(estado);
        categoriaRepository.save(categoria);
    }

    @Override
    public void eliminar(Long farmaciaId, Long id){
        //lógica de borrado lógico
        this.cambiarEstado(farmaciaId, id, false);
    }
}