package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.kardex.KardexResponseDTO;
import farmacias.AppOchoa.model.*;
import farmacias.AppOchoa.repository.KardexRepository;
import farmacias.AppOchoa.services.KardexService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KardexServiceImpl implements KardexService {

    private final KardexRepository kardexRepository;

    @Override
    public void registrarMovimiento(Producto producto, Farmacia farmacia, Usuario usuario,
                                    Integer cantidad, TipoMovimiento tipoMovimiento,
                                    Integer stockAnterior, Integer stockResultante,
                                    BigDecimal costoUnitario, Long referenciaId,
                                    String referenciaTipo, String observacion){

        Kardex kardex = Kardex.builder()
                .producto(producto)
                .farmacia(farmacia)
                .usuario(usuario)
                .cantidad(cantidad)
                .tipoMovimiento(tipoMovimiento)
                .stockAnterior(stockAnterior)
                .stockResultante(stockResultante)
                .costoUnitario(costoUnitario)
                .referenciaId(referenciaId)
                .referenciaTipo(referenciaTipo)
                .observacion(observacion)
                .build();
        kardexRepository.save(kardex);

    }

    @Override
    public List<KardexResponseDTO> obtenerKardex(Long productoId, Long farmaciaId) {
        return kardexRepository.findByProductoAndFarmacia(productoId, farmaciaId)
                .stream()
                .map(KardexResponseDTO::fromEntity)
                .toList();
    }


}
