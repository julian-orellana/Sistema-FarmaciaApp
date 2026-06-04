package farmacias.AppOchoa.dto.categoria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class CategoriaResponseDTO {
    private Long categoria;
    private String nombre;
    private Boolean estado;

    public static CategoriaResponseDTO fromEntity(farmacias.AppOchoa.model.Categoria categoria){
        return CategoriaResponseDTO.builder()
                .categoria(categoria.getCategoriaId())
                .nombre(categoria.getCategoriaNombre())
                .estado(categoria.getCategoriaEstado())
                .build();

    }
}
