package farmacias.AppOchoa.dto.categoria;

import farmacias.AppOchoa.model.Categoria;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CategoriaSimpleDTO {

    private Long categoriaId;
    private String nombre;


    public static CategoriaSimpleDTO fromEntity(Categoria categoria){
        return CategoriaSimpleDTO.builder()
                .categoriaId(categoria.getCategoriaId())
                .nombre(categoria.getCategoriaNombre())
                .build();
    }
}
