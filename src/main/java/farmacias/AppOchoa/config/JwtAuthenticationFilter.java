package farmacias.AppOchoa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import farmacias.AppOchoa.model.Usuario;
import farmacias.AppOchoa.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules(); // Registra JavaTimeModule para LocalDateTime

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Sin header Authorization — puede ser endpoint público, continúa
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        final String username;

        try {
            username = jwtUtil.extractUsername(jwt);
        } catch (ExpiredJwtException e) {
            log.warn("[JWT] Token expirado en {}: {}", request.getRequestURI(), e.getMessage());
            escribirErrorJson(response, HttpStatus.UNAUTHORIZED, "El token ha expirado. Inicia sesión nuevamente.");
            return;
        } catch (MalformedJwtException e) {
            log.warn("[JWT] Token malformado en {}: {}", request.getRequestURI(), e.getMessage());
            escribirErrorJson(response, HttpStatus.UNAUTHORIZED, "El token es inválido o está malformado.");
            return;
        } catch (SignatureException e) {
            log.warn("[JWT] Firma de token inválida en {}: {}", request.getRequestURI(), e.getMessage());
            escribirErrorJson(response, HttpStatus.UNAUTHORIZED, "La firma del token no es válida.");
            return;
        } catch (Exception e) {
            log.warn("[JWT] Error inesperado procesando token en {}: {}", request.getRequestURI(), e.getMessage());
            escribirErrorJson(response, HttpStatus.UNAUTHORIZED, "Token inválido.");
            return;
        }

        // Solo autentica si el SecurityContext está vacío (evita doble procesamiento)
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            Usuario usuario = (Usuario) userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, usuario.getUsername())) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        usuario,
                        null,
                        usuario.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void escribirErrorJson(HttpServletResponse response, HttpStatus status, String mensaje)
            throws IOException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("estado", status.value());
        body.put("mensaje", mensaje);

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}