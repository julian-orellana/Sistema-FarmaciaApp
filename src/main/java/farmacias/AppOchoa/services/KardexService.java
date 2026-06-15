package farmacias.AppOchoa.services;

import farmacias.AppOchoa.dto.kardex.KardexResponseDTO;
import farmacias.AppOchoa.model.Farmacia;
import farmacias.AppOchoa.model.Producto;
import farmacias.AppOchoa.model.TipoMovimiento;
import farmacias.AppOchoa.model.Usuario;

import java.math.BigDecimal;
import java.util.List;

public interface KardexService {
    void registrarMovimiento(Producto producto, Farmacia farmacia, Usuario usuario,
                             Integer cantidad, TipoMovimiento tipoMovimiento,
                             Integer stockAnterior, Integer stockResultante,
                             BigDecimal costoUnitario, Long referenciaId,
                             String referenciaTipo, String observacion);

    List<KardexResponseDTO> obtenerKardex(Long productoId, Long farmaciaId);
}
