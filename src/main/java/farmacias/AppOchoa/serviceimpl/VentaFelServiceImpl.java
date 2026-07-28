package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.ventafel.VentaFelCreateDTO;
import farmacias.AppOchoa.dto.ventafel.VentaFelResponseDTO;
import farmacias.AppOchoa.dto.ventafel.VentaFelSimpleDTO;
import farmacias.AppOchoa.exception.BadRequestException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.integracionfel.*;
import farmacias.AppOchoa.model.*;
import farmacias.AppOchoa.repository.*;
import farmacias.AppOchoa.services.VentaFelService;
import farmacias.AppOchoa.services.VentaService;
import farmacias.AppOchoa.util.EncryptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@Transactional
public class VentaFelServiceImpl implements VentaFelService {
    private final VentaFelRepository ventaFelRepository;
    private final VentaRepository ventaRepository;
    private final SucursalRepository sucursalRepository;
    private final VentaFelNotasCreditoRepository ventaFelNotasCreditoRepository;
    private final CredencialesRepository credencialesRepository;
    private final EncryptionService encryptionService;
    private final DteXmlBuilder dteXmlBuilder;
    private final TekraSoapBuilder tekraSoapBuilder;
    private final TekraClient tekraClient;
    private final TekraResponseParser tekraResponseParser;
    private final DteXmlAnulacionBuilder dteXmlAnulacionBuilder;
    private final VentaService ventaService;

    public VentaFelServiceImpl(
            VentaFelRepository ventaFelRepository,
            VentaRepository ventaRepository,
            SucursalRepository sucursalRepository,
            VentaFelNotasCreditoRepository ventaFelNotasCreditoRepository,
            CredencialesRepository credencialesRepository,
            EncryptionService encryptionService,
            DteXmlBuilder dteXmlBuilder,
            TekraSoapBuilder tekraSoapBuilder,
            TekraClient tekraClient,
            TekraResponseParser tekraResponseParser, DteXmlAnulacionBuilder dteXmlAnulacionBuilder, VentaService ventaService){
        this.ventaFelRepository = ventaFelRepository;
        this.ventaRepository = ventaRepository;
        this.sucursalRepository = sucursalRepository;
        this.ventaFelNotasCreditoRepository = ventaFelNotasCreditoRepository;
        this.credencialesRepository = credencialesRepository;
        this.encryptionService = encryptionService;
        this.dteXmlBuilder = dteXmlBuilder;
        this.tekraSoapBuilder = tekraSoapBuilder;
        this.tekraClient = tekraClient;
        this.tekraResponseParser = tekraResponseParser;
        this.dteXmlAnulacionBuilder = dteXmlAnulacionBuilder;
        this.ventaService = ventaService;
    }

    private Sucursal buscarSucursal(Long farmaciaId){
        return sucursalRepository.findByFarmacia_FarmaciaId(farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada para tu farmacia"));
    }

    @Override
    public VentaFelResponseDTO crear(Long farmaciaId, VentaFelCreateDTO dto){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        Venta venta = buscarVenta(sucursal.getSucursalId(), dto.getVentaId());

        VentaFel ventaFel = VentaFel.builder()
                .venta(venta)
                .farmacia(sucursal.getFarmacia())
                .sucursal(sucursal)
                .build();
        return VentaFelResponseDTO.fromEntity(ventaFelRepository.save(ventaFel));
    }

    private Venta buscarVenta(Long sucursalId, Long id){
        if(id == null) return null;
        return ventaRepository.findByVentaIdAndSucursal_SucursalId(id, sucursalId)
                .orElseThrow(()-> new ResourceNotFoundException("Venta no encontrada en tu farmacia"));
    }

    @Override
    @Transactional(readOnly = true)
    public VentaFelResponseDTO buscarPorId(Long farmaciaId, Long id){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return ventaFelRepository.findByFelIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .map(VentaFelResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Documento FEL no encontrado por ID"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaFelSimpleDTO> listarActivas(Long farmaciaId, Pageable pageable){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return ventaFelRepository.findBySucursal_SucursalId(sucursal.getSucursalId(), pageable)
                .map(VentaFelSimpleDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaFelSimpleDTO> buscarPorTexto(Long farmaciaId, String texto, Pageable pageable) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return ventaFelRepository.buscarPorTexto(sucursal.getSucursalId(), texto, pageable)
                .map(VentaFelSimpleDTO::fromEntity);
    }

    @Override
    public void eliminar(Long farmaciaId, Long id) {
        throw new UnsupportedOperationException("Por reglas de auditoría financiera, este registro es histórico y no puede ser eliminado ni modificado.");
    }

    @Override
    public VentaFelResponseDTO certificar(Long farmaciaId, Long id) {
        Sucursal sucursal = buscarSucursal(farmaciaId);
        VentaFel ventaFel = ventaFelRepository.findByFelIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Documento FEL no encontrado por ID"));

        Venta venta = ventaFel.getVenta();

        CredencialesFel credenciales = credencialesRepository
                .findBySucursal_SucursalIdAndAmbienteAndActivaTrue(sucursal.getSucursalId(), "pruebas")
                .orElseThrow(() -> new ResourceNotFoundException("No hay credenciales FEL activas para esta sucursal"));

        try {
            String usuarioPlano = encryptionService.decrypt(credenciales.getCredencialUsuarioCifrado());
            String clavePlano = encryptionService.decrypt(credenciales.getCredencialSecretoCifrado());

            String dteXml = dteXmlBuilder.construir(venta);
            String sobreSoap = tekraSoapBuilder.construirSobre(credenciales, usuarioPlano, clavePlano, dteXml);
            String respuestaSoap = tekraClient.certificar(sobreSoap);
            TekraCertificacionResultado resultado = tekraResponseParser.parsear(respuestaSoap);

            if (resultado.isExitoso()) {
                ventaFel.setFelEstado(VentaFelEstado.CERTIFICADA);
                ventaFel.setFelUuid(resultado.getUuid());
                ventaFel.setFelNumeroAutorizacion(resultado.getSerie() + "-" + resultado.getNumero());
                ventaFel.setFelFechaCertificacion(OffsetDateTime.parse(resultado.getFechaCertificacion()).toLocalDateTime());
                ventaFel.setFelXmlDte(resultado.getXmlCertificado());
                ventaFel.setFelErrorDescripcion(null);
                ventaFel.setFelSerie(resultado.getSerie());
                ventaFel.setFelNumeroDocumento(resultado.getNumero());
            } else {
                ventaFel.setFelEstado(VentaFelEstado.ERROR);
                ventaFel.setFelErrorDescripcion(resultado.getMensajeError());
            }
        } catch (Exception e) {
            ventaFel.setFelEstado(VentaFelEstado.ERROR);
            ventaFel.setFelErrorDescripcion("Error de comunicación con TEKRA: " + e.getMessage());
        }

        ventaFel.setFelIntentos(ventaFel.getFelIntentos() == null ? 1 : ventaFel.getFelIntentos() + 1);

        return VentaFelResponseDTO.fromEntity(ventaFelRepository.save(ventaFel));
    }


    @Override
    @Transactional
    public VentaFelResponseDTO anular(Long farmaciaId, Long id, String motivo){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        VentaFel ventaFel = ventaFelRepository.findByFelIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Documento FEL no encontrado por ID"));

        Venta venta = ventaFel.getVenta();

        //Para anular ante tekra, se necesita el uuid de la factura.
        //(el uuid solo existe si la factura se certificó)
        if(ventaFel.getFelEstado() != VentaFelEstado.CERTIFICADA){
            throw new BadRequestException("Solo se puede anular un documento certificado");
        }

        //Regla de negocio (No anular factura si esta misma ya cuenta con una nota de crédito)
        if(ventaFelNotasCreditoRepository.existsByVentaFel_FelIdAndNotaEstado(id, NotaEstado.CERTIFICADA)){
            throw new BadRequestException("La factura tiene nota de crédito, no puede anularse");
        }

        CredencialesFel credenciales = credencialesRepository
                .findBySucursal_SucursalIdAndAmbienteAndActivaTrue(sucursal.getSucursalId(), "pruebas")
                .orElseThrow(() -> new ResourceNotFoundException("No hay credenciales FEL activas en esta sucursal"));



        try{
            String usuarioPlano = encryptionService.decrypt(credenciales.getCredencialUsuarioCifrado());
            String clavePlano = encryptionService.decrypt(credenciales.getCredencialSecretoCifrado());

            String dteXml = dteXmlAnulacionBuilder.construir(ventaFel, motivo);
            String sobreSoap = tekraSoapBuilder.construirSobreAnulacion(credenciales, usuarioPlano,  clavePlano, dteXml);
            String respuestaSoap = tekraClient.anular(sobreSoap);
            TekraCertificacionResultado resultado = tekraResponseParser.parsearAnulacion(respuestaSoap);

            if(resultado.isExitoso()) {
                ventaFel.setFelEstado(VentaFelEstado.ANULADA);
                ventaService.cambiarEstado(farmaciaId, venta.getVentaId(), VentaEstado.anulada);
            }else {
                ventaFel.setFelEstado(VentaFelEstado.ERROR);
                ventaFel.setFelErrorDescripcion(resultado.getMensajeError());
            }
        }catch (Exception e) {
            ventaFel.setFelEstado(VentaFelEstado.ERROR);
            ventaFel.setFelErrorDescripcion("Error de comunicación con TEKRA: " + e.getMessage());
        }
        ventaFel.setFelIntentos(ventaFel.getFelIntentos() == null ? 1 : ventaFel.getFelIntentos() + 1);

        return VentaFelResponseDTO.fromEntity(ventaFelRepository.save(ventaFel));


    }

}