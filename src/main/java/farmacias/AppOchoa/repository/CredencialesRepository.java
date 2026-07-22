package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.CredencialesFel;
import farmacias.AppOchoa.model.VentaFel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CredencialesRepository extends JpaRepository<CredencialesFel, Long>{

    Optional<CredencialesFel> findByCredencialIdAndSucursal_SucursalId(Long credencialId, Long sucursalId);
    Optional<CredencialesFel> findByCredencialCliente(Long credencialCliente);
    boolean existsByCredencialCliente(Long credencialCliente);
    List<CredencialesFel> findBySucursal_SucursalId(Long sucursalId);
    Optional<CredencialesFel> findBySucursal_SucursalIdAndAmbienteAndActivaTrue(Long sucursalId, String ambiente);

}
