package farmacias.AppOchoa.dto.sucursal;

import farmacias.AppOchoa.model.Sucursal;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SucursalSimpleDTO {

    private Long sucursalId;
    private String nombre;
    private String sucursalNit;
    private String direccion;


    public static SucursalSimpleDTO fromEntity(Sucursal sucursal) {
        return SucursalSimpleDTO.builder()
                .sucursalId(sucursal.getSucursalId())
                .nombre(sucursal.getSucursalNombre())
                .sucursalNit(sucursal.getSucursalNit())
                .direccion(sucursal.getSucursalDireccion())
                .build();
    }
}