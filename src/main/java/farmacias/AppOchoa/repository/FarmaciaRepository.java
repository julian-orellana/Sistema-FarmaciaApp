package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.Farmacia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FarmaciaRepository extends JpaRepository<Farmacia, Long> {

    Page<Farmacia> findByFarmaciaActivaTrue(Pageable pageable);
    boolean existsByFarmaciaTelefono(String telefono);

    @Query("SELECT f FROM Farmacia f WHERE " +
            "LOWER(f.farmaciaNombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(f.farmaciaTelefono) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Farmacia> buscarPorTexto(@Param("texto") String texto, Pageable pageable);
}