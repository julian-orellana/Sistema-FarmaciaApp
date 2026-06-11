package farmacias.AppOchoa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filtro de rate limiting para el endpoint de login.
 *
 * <p>Implementa una ventana deslizante por IP: si una dirección supera
 * {@value MAX_INTENTOS} intentos en {@value VENTANA_MS} ms, recibe HTTP 429
 * hasta que el intento más antiguo salga de la ventana.
 *
 * <p>Opera antes del procesamiento de autenticación, por lo que bloquea
 * ataques de fuerza bruta sin llegar a consultar la base de datos.
 */
@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LoginRateLimitFilter.class);

    // ObjectMapper compartido; findAndRegisterModules() habilita soporte para LocalDateTime.
    private static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private static final String LOGIN_PATH   = "/api/v1/auth/login";
    private static final int    MAX_INTENTOS = 10;
    private static final long   VENTANA_MS   = 60_000L;

    /**
     * IPs desde las que se confía en el header X-Forwarded-For.
     *
     * <p>En el despliegue Docker, Nginx corre en el mismo host que la app y
     * se conecta por loopback. Solo desde esas direcciones el header es
     * controlado por el proxy y no por el cliente, por lo que es seguro usarlo
     * para obtener la IP real del usuario final.
     */
    private static final Set<String> PROXIES_CONFIABLES = Set.of(
            "127.0.0.1",
            "0:0:0:0:0:0:0:1",
            "::1");

    // Mapa de IP → cola de timestamps de intentos dentro de la ventana activa.
    private final ConcurrentHashMap<String, Deque<Long>> intentosPorIp = new ConcurrentHashMap<>();

    /**
     * Punto de entrada del filtro. Solo actúa sobre POST /api/v1/auth/login;
     * cualquier otra ruta o método pasa sin inspección.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!LOGIN_PATH.equals(request.getRequestURI()) || !"POST".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip   = obtenerIp(request);
        long   ahora = System.currentTimeMillis();

        Deque<Long> timestamps = intentosPorIp.computeIfAbsent(ip, k -> new ArrayDeque<>());

        synchronized (timestamps) {

            // Descartar intentos que ya salieron de la ventana deslizante.
            while (!timestamps.isEmpty() && ahora - timestamps.peekFirst() > VENTANA_MS) {
                timestamps.pollFirst();
            }

            if (timestamps.size() >= MAX_INTENTOS) {
                // Calcular cuántos segundos faltan para que el intento más antiguo expire.
                long segundosRestantes = (VENTANA_MS - (ahora - timestamps.peekFirst())) / 1000;
                log.warn("[RateLimit] IP {} bloqueada por exceso de intentos de login. Espera: {}s", ip, segundosRestantes);
                escribirErrorJson(response, segundosRestantes);
                return;
            }

            // Registrar este intento antes de dejar pasar la petición.
            timestamps.addLast(ahora);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Job de limpieza periódica del mapa de IPs.
     *
     * <p>Sin esta evicción, el mapa acumularía una entrada por cada IP que
     * alguna vez tocó el endpoint, incluyendo bots de un solo intento.
     * Se elimina toda IP cuya última actividad supere la duración de la ventana.
     */
    @Scheduled(fixedDelay = 300_000)
    public void limpiarIpsInactivas() {
        long ahora = System.currentTimeMillis();
        intentosPorIp.entrySet().removeIf(entry -> {
            Deque<Long> timestamps = entry.getValue();
            synchronized (timestamps) {
                return timestamps.isEmpty() || ahora - timestamps.peekLast() > VENTANA_MS;
            }
        });
    }

    /**
     * Resuelve la IP de origen de la petición.
     *
     * <p>Si la conexión directa proviene de un proxy de confianza, se lee el
     * último valor de X-Forwarded-For, que es el que el proxy mismo appendeó
     * (los valores anteriores pueden estar falsificados por el cliente).
     * Si la conexión es directa, se usa {@code remoteAddr} sin condiciones.
     */
    private String obtenerIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();

        if (PROXIES_CONFIABLES.contains(remoteAddr)) {
            String xff = request.getHeader("X-Forwarded-For");
            if (xff != null && !xff.isBlank()) {
                String[] partes = xff.split(",");
                return partes[partes.length - 1].trim();
            }
        }

        return remoteAddr;
    }

    /**
     * Escribe una respuesta HTTP 429 en formato JSON con el tiempo de espera
     * restante. Termina el ciclo del filtro; la petición no llega al controller.
     */
    private void escribirErrorJson(HttpServletResponse response, long segundosRestantes)
            throws IOException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("estado", HttpStatus.TOO_MANY_REQUESTS.value());
        body.put("mensaje", String.format(
                "Demasiados intentos de login. Intenta nuevamente en %d segundos.", segundosRestantes));

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}