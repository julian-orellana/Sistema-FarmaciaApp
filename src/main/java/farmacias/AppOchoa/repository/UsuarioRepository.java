package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByNombreUsuarioUsuario(String nombreUsuarioUsuario);

    // Carga farmacia en la misma query: el login y el filtro JWT validan la
    // vigencia de la suscripción fuera de la sesión de Hibernate (open-in-view: false)
    @EntityGraph(attributePaths = "farmacia")
    Optional<Usuario> findByNombreUsuarioUsuario(String nombreUsuarioUsuario);
    Page<Usuario> findByUsuarioEstadoTrue(Pageable pageable);
    Page<Usuario> findByFarmacia_FarmaciaIdAndUsuarioEstadoTrue(Long farmaciaId, Pageable pageable);
    boolean existsByFarmacia_FarmaciaIdAndNombreUsuarioUsuario(Long farmaciaId, String nombreUsuario);
    Page<Usuario> findByFarmacia_FarmaciaId(Long farmaciaId, Pageable pageable);
    Optional<Usuario> findByUsuarioIdAndFarmacia_FarmaciaId(Long usuarioId, Long farmaciaId);
    long countByFarmacia_FarmaciaIdAndUsuarioEstadoTrue(Long farmaciaId);


    @Query("SELECT u FROM Usuario u WHERE " +
            "LOWER(u.usuarioNombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(u.usuarioApellido) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(u.nombreUsuarioUsuario) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Usuario> buscarPorTexto(@Param("texto") String texto, Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE u.farmacia.farmaciaId = :farmaciaId AND (" +
            "LOWER(u.usuarioNombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(u.usuarioApellido) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(u.nombreUsuarioUsuario) LIKE LOWER(CONCAT('%', :texto, '%')))")
    Page<Usuario> buscarPorTexto(@Param("farmaciaId") Long farmaciaId, @Param("texto") String texto, Pageable pageable);
}