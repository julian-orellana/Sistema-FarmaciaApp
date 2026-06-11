package farmacias.AppOchoa.config;

import farmacias.AppOchoa.model.*;
import farmacias.AppOchoa.repository.FarmaciaRepository;
import farmacias.AppOchoa.repository.SucursalRepository;
import farmacias.AppOchoa.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.UUID;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initData(
            UsuarioRepository usuarioRepository,
            FarmaciaRepository farmaciaRepository,
            SucursalRepository sucursalRepository,
            PasswordEncoder passwordEncoder,
            JwtConfig jwtConfig
    ) {
        return args -> {

            // Validar configuración JWT al arranque
            jwtConfig.validateConfig();

            // Solo ejecutar bootstrap si no existe ninguna farmacia en el sistema
            if (farmaciaRepository.count() > 0) {
                log.info("[DataInitializer] Sistema ya inicializado. Omitiendo bootstrap.");
                return;
            }

            log.warn("[DataInitializer] Base de datos vacía detectada. Ejecutando bootstrap inicial...");

            // 1. Crear farmacia base del sistema
            Farmacia farmacia = Farmacia.builder()
                    .farmaciaNombre("Farmacia Principal")
                    .farmaciaNit("000000000")
                    .farmaciaEmail("admin@farmacloud.software")
                    .farmaciaTelefono("00000000")
                    .planTipo(PlanTipo.pro)
                    .farmaciaActiva(true)
                    .enPeriodoPrueba(true)
                    .pruebaHasta(LocalDate.now().plusDays(30))
                    .suscripcionVigencia(LocalDate.now().plusDays(30))
                    .maxSucursales(3)
                    .maxUsuarios(5)
                    .build();

            farmacia = farmaciaRepository.save(farmacia);
            log.warn("[DataInitializer] Farmacia base creada con ID: {}", farmacia.getFarmaciaId());

            // 2. Crear sucursal base
            Sucursal sucursal = Sucursal.builder()
                    .sucursalNombre("Sucursal Central")
                    .sucursalDireccion("Dirección pendiente de actualizar")
                    .sucursalTelefono("00000000")
                    .sucursalEstado(true)
                    .farmacia(farmacia)
                    .build();

            sucursal = sucursalRepository.save(sucursal);
            log.warn("[DataInitializer] Sucursal base creada con ID: {}", sucursal.getSucursalId());

            // 3. Crear usuario administrador vinculado a la farmacia y sucursal
            if (!usuarioRepository.existsByNombreUsuarioUsuario("admin")) {
                String passwordInicial = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
                Usuario admin = Usuario.builder()
                        .nombreUsuarioUsuario("admin")
                        .usuarioContrasenaHash(passwordEncoder.encode(passwordInicial))
                        .usuarioNombre("Administrador")
                        .usuarioApellido("Sistema")
                        .usuarioRol(UsuarioRol.administrador)
                        .usuarioEstado(true)
                        .farmacia(farmacia)
                        .sucursal(sucursal)
                        .build();

                usuarioRepository.save(admin);
                log.warn("[DataInitializer] ¡¡¡ USUARIO ADMIN CREADO - CAMBIA LA CONTRASEÑA INMEDIATAMENTE !!!");
                // La contraseña va por stdout, NO por el logger: en prod el root logger
                // WARN tiene appender Loki (Grafana Cloud) y persistiria la credencial
                // en un servicio externo. stdout queda solo en `docker logs`.
                System.out.println("[DataInitializer] Contraseña temporal del admin (solo visible aquí): " + passwordInicial);
            }

            log.warn("[DataInitializer] Bootstrap completado. Actualiza datos de farmacia y contraseña del admin.");
        };
    }
}