package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.cajacorte.CajaCorteCreateDTO;
import farmacias.AppOchoa.dto.cajacorte.CajaCorteResponseDTO;
import farmacias.AppOchoa.dto.cajacorte.CajaCorteSimpleDTO;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.*;
import farmacias.AppOchoa.repository.*;
import farmacias.AppOchoa.services.CajaCorteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class CajaCorteServiceImpl implements CajaCorteService {

    private final CajaCortesRepository cajaCortesRepository;
    private final CajaSesionesRepository cajaSesionesRepository;
    private final UsuarioRepository usuarioRepository;
    private final VentaPagoRepository ventaPagoRepository;
    private final FarmaciaRepository farmaciaRepository;

    public CajaCorteServiceImpl(
            CajaCortesRepository cajaCortesRepository,
            CajaSesionesRepository cajaSesionesRepository,
            UsuarioRepository usuarioRepository,
            VentaPagoRepository ventaPagoRepository,
            FarmaciaRepository farmaciaRepository) {
        this.cajaCortesRepository = cajaCortesRepository;
        this.cajaSesionesRepository = cajaSesionesRepository;
        this.usuarioRepository = usuarioRepository;
        this.ventaPagoRepository = ventaPagoRepository;
        this.farmaciaRepository = farmaciaRepository;
    }

    @Override
    public CajaCorteResponseDTO crear(Long farmaciaId, CajaCorteCreateDTO dto) {
        CajaSesiones cajaSesiones = buscarSesiones(farmaciaId, dto.getSesionId());
        Usuario usuario = buscarUsuario(farmaciaId, dto.getUsuarioSupervisorId());

        BigDecimal totalCredito = ventaPagoRepository.sumarPorSesionYMetodo(cajaSesiones.getSesionId(), MetodoPagoEstado.tarjetaDeCredito);
        BigDecimal totalDebito = ventaPagoRepository.sumarPorSesionYMetodo(cajaSesiones.getSesionId(), MetodoPagoEstado.tarjetaDeDebito);
        BigDecimal totalVentas = ventaPagoRepository.sumarTotalPorSesion(cajaSesiones.getSesionId());

        Farmacia farmacia = farmaciaRepository.getReferenceById(farmaciaId);

        CajaCorte cajaCorte = CajaCorte.builder()
                .cajaSesiones(cajaSesiones)
                .usuario(usuario)
                .corteTotalEfectivo(dto.getEfectivoFisicoContado())
                .corteTotalTarjetaCredito(totalCredito)
                .corteTotalTarjetaDebito(totalDebito)
                .corteTotalVentas(totalVentas)
                .farmacia(farmacia)
                .build();

        return CajaCorteResponseDTO.fromEntity(cajaCortesRepository.save(cajaCorte));
    }
    // Metodos Auxiliares
    private CajaSesiones buscarSesiones(Long farmaciaId, Long id) {
        if (id == null) return null;
        return cajaSesionesRepository.findBySesionIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sesion no encontrada en tu farmacia"));
    }

    private Usuario buscarUsuario(Long farmaciaId, Long id) {
        if (id == null) return null;
        return usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado en tu farmacia"));
    }
    @Override
    @Transactional(readOnly = true)
    public Page<CajaCorteSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable) {
        return cajaCortesRepository.buscarPorTexto(farmaciaId, texto, pageable)
                .map(CajaCorteSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public CajaCorteResponseDTO buscarPorId(Long farmaciaId, Long id) {
        return cajaCortesRepository.findByCorteIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .map(CajaCorteResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Corte no encontrado por ID"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CajaCorteSimpleDTO> listarCortes(Long farmaciaId, Pageable pageable) {
        return cajaCortesRepository.findByFarmacia_FarmaciaId(farmaciaId, pageable)
                .map(CajaCorteSimpleDTO::fromEntity);
    }

    @Override
    public void eliminar(Long farmaciaId, Long id) {
        throw new UnsupportedOperationException("Por reglas de auditoría financiera, este registro es histórico y no puede ser eliminado ni modificado.");
    }
}