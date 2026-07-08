package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.categoria.CategoriaCreateDTO;
import farmacias.AppOchoa.dto.categoria.CategoriaResponseDTO;
import farmacias.AppOchoa.dto.categoria.CategoriaSimpleDTO;
import farmacias.AppOchoa.dto.categoria.CategoriaUpdateDTO;
import farmacias.AppOchoa.exception.DuplicateResourceException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.Categoria;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.Sucursal;
import farmacias.AppOchoa.repository.CategoriaRepository;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.repository.SucursalRepository;
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
    private final SucursalRepository sucursalRepository;

    public CategoriaServiceImpl(
            CategoriaRepository categoriaRepository,
            SucursalRepository sucursalRepository) {
        this.categoriaRepository = categoriaRepository;
        this.sucursalRepository = sucursalRepository;
    }

    @Override
    public CategoriaResponseDTO crear(Long farmaciaId, CategoriaCreateDTO dto) {
        Sucursal sucursal = buscarSucursal(farmaciaId);

        if (categoriaRepository.existsBySucursal_SucursalIdAndCategoriaNombre(sucursal.getSucursalId(), dto.getNombre())) {
            throw new DuplicateResourceException("Ya existe una categoría con ese nombre: " + dto.getNombre());
        }

        Categoria categoria = Categoria.builder()
                .categoriaNombre(dto.getNombre())
                .categoriaEstado(true)
                .sucursal(sucursal)
                .build();

        Categoria guardada = categoriaRepository.save(categoria);
        return CategoriaResponseDTO.fromEntity(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDTO obtenerPorId(Long farmaciaId, Long id) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        Categoria categoria = categoriaRepository.findByCategoriaIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        return CategoriaResponseDTO.fromEntity(categoria);
    }

    //Método auxiliar
    private Sucursal buscarSucursal(Long farmaciaId){
        return sucursalRepository.findByFarmacia_FarmaciaId(farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada para tu farmacia"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoriaSimpleDTO> listarTodasPaginadas(Long farmaciaId, Pageable pageable) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return categoriaRepository.findBySucursal_SucursalId(sucursal.getSucursalId(), pageable)
                .map(CategoriaSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoriaSimpleDTO> listarActivasPaginadas(Long farmaciaId, Pageable pageable) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return categoriaRepository.findBySucursal_SucursalId(sucursal.getSucursalId(), pageable)
                .map(CategoriaSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoriaSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return categoriaRepository.buscarPorTexto(sucursal.getSucursalId(), texto, pageable)
                .map(CategoriaSimpleDTO::fromEntity);
    }

    @Override
    public CategoriaResponseDTO actualizar(Long farmaciaId, Long id, CategoriaUpdateDTO dto){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        Categoria categoria = categoriaRepository.findByCategoriaIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .orElseThrow(()-> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        if(!categoria.getCategoriaNombre().equals(dto.getNombre())){
            if(categoriaRepository.existsBySucursal_SucursalIdAndCategoriaNombre(sucursal.getSucursalId(), dto.getNombre())){
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
        Sucursal sucursal = buscarSucursal(farmaciaId);
        Categoria categoria = categoriaRepository.findByCategoriaIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
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