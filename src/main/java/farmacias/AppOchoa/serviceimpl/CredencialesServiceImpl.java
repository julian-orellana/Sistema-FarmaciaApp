package farmacias.AppOchoa.serviceimpl;

import farmacias.AppOchoa.dto.credencialesfel.CredencialesCreateDTO;
import farmacias.AppOchoa.dto.credencialesfel.CredencialesResponseDTO;
import farmacias.AppOchoa.dto.credencialesfel.CredencialesSimpleDTO;
import farmacias.AppOchoa.dto.credencialesfel.CredencialesUpdateDTO;
import farmacias.AppOchoa.exception.DuplicateResourceException;
import farmacias.AppOchoa.exception.ResourceNotFoundException;
import farmacias.AppOchoa.model.CredencialesFel;
import farmacias.AppOchoa.model.Sucursal;
import farmacias.AppOchoa.repository.CredencialesRepository;
import farmacias.AppOchoa.repository.SucursalRepository;
import farmacias.AppOchoa.services.CredencialesService;
import farmacias.AppOchoa.util.EncryptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CredencialesServiceImpl implements CredencialesService {

    private final CredencialesRepository credencialesRepository;
    private final SucursalRepository sucursalRepository;
    private final EncryptionService encryptionService;

    public CredencialesServiceImpl(
            CredencialesRepository credencialesRepository,
            SucursalRepository sucursalRepository,
            EncryptionService encryptionService){
        this.credencialesRepository = credencialesRepository;
        this.sucursalRepository = sucursalRepository;
        this.encryptionService = encryptionService;
    }

    @Override
    public CredencialesResponseDTO crear(Long farmaciaId, CredencialesCreateDTO dto){
        if(credencialesRepository.existsByCredencialCliente(dto.getCredencialCliente())){
            throw new DuplicateResourceException("La credencial cliente ya esta en uso" + dto.getCredencialCliente());
        }

        Sucursal sucursal = buscarSucursal(farmaciaId);
        CredencialesFel credencialesFel = CredencialesFel.builder()

                .certificador(dto.getCertificador())
                .credencialUsuarioCifrado(encryptionService.encrypt(dto.getCredencialUsuario()))
                .credencialSecretoCifrado(encryptionService.encrypt(dto.getCredencialSecreto()))
                .credencialExtraCifrado(dto.getCredencialExtra()!= null?
                        encryptionService.encrypt(dto.getCredencialExtra())
                        :  null)
                .credencialCliente(dto.getCredencialCliente())
                .credencialContrato(dto.getCredencialContrato())
                .firmarEmisor(dto.getFirmarEmisor())
                .ambiente(dto.getAmbiente())
                .validarIdentificador(dto.getValidarIdentificador())
                .activa(false)
                .sucursal(sucursal)
                .build();

        return CredencialesResponseDTO.fromEntity(credencialesRepository.save(credencialesFel));
    }

    @Override
    public CredencialesResponseDTO actualizar(Long farmaciaId, Long id, CredencialesUpdateDTO dto){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        CredencialesFel credencialesFel = credencialesRepository.findByCredencialIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Credencial no encontrada"));

        if(!credencialesFel.getCredencialCliente().equals(dto.getCredencialCliente())
            && credencialesRepository.existsByCredencialCliente(dto.getCredencialCliente())){
            throw new DuplicateResourceException("La credencial del cliente ya esta en uso");
        }


        credencialesFel.setCredencialUsuarioCifrado(encryptionService.encrypt(dto.getCredencialUsuario()));
        credencialesFel.setCredencialSecretoCifrado(encryptionService.encrypt(dto.getCredencialSecreto()));
        credencialesFel.setCredencialExtraCifrado(dto.getCredencialExtra() != null ?
                encryptionService.encrypt(dto.getCredencialExtra()): null);
        credencialesFel.setCredencialCliente(dto.getCredencialCliente());
        credencialesFel.setCredencialContrato(dto.getCredencialContrato());
        credencialesFel.setFirmarEmisor(dto.getFirmarEmisor());
        credencialesFel.setValidarIdentificador(dto.getValidarIdentificador());
        credencialesFel.setActiva(dto.getActiva());

        CredencialesResponseDTO responseDTO = CredencialesResponseDTO.fromEntity
                (credencialesRepository.save(credencialesFel));
        return responseDTO;

    }

    @Override
    @Transactional(readOnly = true)
    public CredencialesResponseDTO buscarPorId(Long farmaciaId, Long id){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return credencialesRepository.findByCredencialIdAndSucursal_SucursalId(id, sucursal.getSucursalId())
                .map(CredencialesResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Credencial no encontrada por id" + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CredencialesSimpleDTO> listar(Long farmaciaId){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        return credencialesRepository.findBySucursal_SucursalId(sucursal.getSucursalId())
                .stream()
                .map(CredencialesSimpleDTO::fromEntity)
                .toList();
    }

    @Override
    public void eliminar(Long farmaciaId, Long id){
        Sucursal sucursal = buscarSucursal(farmaciaId);
        CredencialesFel credencialesFel = credencialesRepository.findByCredencialIdAndSucursal_SucursalId
                (id, sucursal.getSucursalId())
                .orElseThrow(()-> new ResourceNotFoundException("Credencial no encontrada por id" + id));
        credencialesRepository.delete(credencialesFel);
    }


    //Metodo Auxiliar
    private Sucursal buscarSucursal(Long farmaciaId){
        return sucursalRepository.findByFarmacia_FarmaciaId(farmaciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Credencial no encontrada en tu Farmacia"));
    }

}
