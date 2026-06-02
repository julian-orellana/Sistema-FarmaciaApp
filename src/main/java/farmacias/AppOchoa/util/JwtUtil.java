package farmacias.AppOchoa.util;

import farmacias.AppOchoa.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Autowired
    private JwtConfig jwtConfig;

    //Toma el secret como texto "miSecretSuperSeguro123..."
    //Lo convierte a bytes [109, 105, 83, 101, 99...]
    //Lo convierte en llave objeto SecretKey para firmar/verificar
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //Extracción

    public String extractUsername(String token) {
        //extrae el username (subject)
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        //extrae la fecha de expiración
        return extractClaim(token, Claims::getExpiration);
    }

    public Long extractFarmaciaId(String token) {
        //extrae el farmaciaId creado como claim extra
        return extractClaim(token, claims -> claims.get("farmaciaId", Long.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) //verifica que la firma sea válida
                .build()
                .parseSignedClaims(token)// decodifica el token
                .getPayload(); // devuelve todos los claims
    }

    // ── Generación

    //Versión 1 — sin claims extra, solo el username
    public String generateToken(String username) {
        return createToken(Map.of(), username);
    }

    //Versión 2 — con farmaciaId solamente
    public String generateToken(String username, Long farmaciaId) {
        return createToken(Map.of("farmaciaId", farmaciaId), username);
    }

    //Versión 3 — con datos aleatorios
    public String generateToken(Map<String, Object> extraClaims, String username) {
        return createToken(extraClaims, username);
    }

    // Constructor del Token Recibe dos cosas:
    //claims → los datos extra que viajan en el token
    //subject → el username, el dueño del token
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date(); //Tiempo exacto de la generacion del token
        Date expiration = new Date(now.getTime() + jwtConfig.getExpiration());

        return Jwts.builder()
                .claims(claims)               // datos extra: userId, rol, farmaciaId...
                .subject(subject)             // el username
                .issuedAt(now)                // cuándo se creó
                .expiration(expiration)       // cuándo expira
                .signWith(getSigningKey())    // firma con llave secreta
                .compact();                   // construye el string eyJ...
    }

    //Validación

    //valida que el username coincida Y que no esté expirado
    public Boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    //solo valida que no esté expirado — sin importar el username
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false; //token malformado o firma inválida
        }
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}