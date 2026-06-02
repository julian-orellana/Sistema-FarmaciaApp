package farmacias.AppOchoa.repository;

import farmacias.AppOchoa.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    // Revocar todos los tokens de un usuario (logout completo / cambio de contraseña)
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.usuario.usuarioId = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") Long usuarioId);

    // Limpieza periódica de tokens expirados
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiraEn < :ahora")
    void deleteTokensExpirados(@Param("ahora") LocalDateTime ahora);
}