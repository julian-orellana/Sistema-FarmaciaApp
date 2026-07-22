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
    private String sucursalNit;
    private String direccion;
    private String telefono;
    private Boolean estado;
    private String municipio;
    private String departamento;
    private String codigoEstablecimiento;
    private String afiliacionIva;
    private String codigoPostal;
    private LocalDateTime fechaCreacion;

    public static SucursalResponseDTO fromEntity(farmacias.AppOchoa.model.Sucursal sucursal){
        return SucursalResponseDTO.builder()
                .sucursalId(sucursal.getSucursalId())
                .nombre(sucursal.getSucursalNombre())
                .sucursalNit(sucursal.getSucursalNit())
                .direccion(sucursal.getSucursalDireccion())
                .telefono(sucursal.getSucursalTelefono())
                .estado(sucursal.getSucursalEstado())
                .departamento(sucursal.getDepartamento())
                .municipio(sucursal.getMunicipio())
                .codigoEstablecimiento(sucursal.getCodigoEstablecimiento())
                .afiliacionIva(sucursal.getAfiliacionIva())
                .codigoPostal(sucursal.getCodigoPostal())
                .fechaCreacion(sucursal.getAuditoriaFechaCreacion())
                .build();

    }
}
