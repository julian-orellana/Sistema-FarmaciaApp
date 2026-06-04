package farmacias.AppOchoa.dto.sucursal;

import farmacias.AppOchoa.model.Sucursal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SucursalResponseDTO {

    private Long sucursalId;
    private String nombre;
    private String direccion;
    private String telefono;
    private Boolean estado;
    private LocalDateTime fechaCreacion;

    public static SucursalResponseDTO fromEntity(farmacias.AppOchoa.model.Sucursal sucursal){
        return SucursalResponseDTO.builder()
                .sucursalId(sucursal.getSucursalId())
                .nombre(sucursal.getSucursalNombre())
                .direccion(sucursal.getSucursalDireccion())
                .telefono(sucursal.getSucursalTelefono())
                .estado(sucursal.getSucursalEstado())
                .fechaCreacion(sucursal.getAuditoriaFechaCreacion())
                .build();

    }
}
