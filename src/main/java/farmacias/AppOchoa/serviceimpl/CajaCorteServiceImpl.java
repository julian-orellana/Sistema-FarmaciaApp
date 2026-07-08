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
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final SucursalRepository sucursalRepository;

    public CajaCorteServiceImpl(
            CajaCortesRepository cajaCortesRepository,
            CajaSesionesRepository cajaSesionesRepository,
            UsuarioRepository usuarioRepository,
            VentaPagoRepository ventaPagoRepository,
            SucursalRepository sucursalRepository) {
        this.cajaCortesRepository = cajaCortesRepository;
        this.cajaSesionesRepository = cajaSesionesRepository;
        this.usuarioRepository = usuarioRepository;
        this.ventaPagoRepository = ventaPagoRepository;
        this.sucursalRepository = sucursalRepository;
    }

    private Sucursal buscarSucursal(Long farmaciaId) {
        return sucursalRepository.findByFarmacia_FarmaciaId(farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada para tu farmacia"));
    }

    @Override
    public CajaCorteResponseDTO crear(Long farmaciaId, CajaCorteCreateDTO dto) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        CajaSesiones cajaSesiones = buscarSesiones(sucursal.getSucursalId(), dto.getSesionId());

        Usuario solicitante = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = buscarUsuario(farmaciaId, solicitante.getUsuarioId());

        BigDecimal totalCredito = ventaPagoRepository.sumarPorSesionYMetodo(cajaSesiones.getSesionId(), MetodoPagoEstado.TARJETA_CREDITO);
        BigDecimal totalDebito = ventaPagoRepository.sumarPorSesionYMetodo(cajaSesiones.getSesionId(), MetodoPagoEstado.TARJETA_DEBITO);
        BigDecimal totalVentas = ventaPagoRepository.sumarTotalPorSesion(cajaSesiones.getSesionId());

        CajaCorte cajaCorte = CajaCorte.builder()
                .cajaSesiones(cajaSesiones)
                .usuario(usuario)
                .corteTotalEfectivo(dto.getEfectivoFisicoContado())
                .corteTotalTarjetaCredito(totalCredito)
                .corteTotalTarjetaDebito(totalDebito)
                .corteTotalVentas(totalVentas)
                .farmacia(sucursal.getFarmacia())
                .sucursal(sucursal)
                .build();

        return CajaCorteResponseDTO.fromEntity(cajaCortesRepository.save(cajaCorte));
    }

    private CajaSesiones buscarSesiones(Long sucursalId, Long id) {
        if (id == null) return null;
        return cajaSesionesRepository.findBySesionIdAndSucursal_SucursalId(id, sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("Sesión no encontrada en tu farmacia"));
    }

    private Usuario buscarUsuario(Long farmaciaId, Long id) {
        if (id == null) return null;
        return usuarioRepository.findByUsuarioIdAndFarmacia_FarmaciaId(id, farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado en tu farmacia"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CajaCorteSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return cajaCortesRepository.buscarPorTexto(sucursal.getSucursalId(), texto, pageable)
                .map(CajaCorteSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public CajaCorteResponseDTO buscarPorId(Long farmaciaId, Long id) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return cajaCortesRepository.findByCorteIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .map(CajaCorteResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Corte no encontrado por ID"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CajaCorteSimpleDTO> listarCortes(Long farmaciaId, Pageable pageable) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return cajaCortesRepository.findBySucursal_SucursalId(sucursal.getSucursalId(), pageable)
                .map(CajaCorteSimpleDTO::fromEntity);
    }

    @Override
    public void eliminar(Long farmaciaId, Long id) {
        throw new UnsupportedOperationException("Por reglas de auditoría financiera, este registro es histórico y no puede ser eliminado ni modificado.");
    }
}