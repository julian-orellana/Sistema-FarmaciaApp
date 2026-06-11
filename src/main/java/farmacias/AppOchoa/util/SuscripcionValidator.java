package farmacias.AppOchoa.util;

import farmacias.AppOchoa.exception.SuscripcionVencidaException;
import farmacias.AppOchoa.model.Farmacia;

import java.time.LocalDate;

/**
 * Valida que la farmacia esté activa y con suscripción/periodo de prueba vigente (A8).
 * Se usa tanto en el login como en el filtro JWT para que un token emitido antes
 * del vencimiento deje de servir cuando la suscripción caduca.
 */
public final class SuscripcionValidator {

    private SuscripcionValidator() {
    }

    public static void validarVigencia(Farmacia farmacia) {
        if (Boolean.FALSE.equals(farmacia.getFarmaciaActiva())) {
            throw new SuscripcionVencidaException("Tu farmacia está desactivada. Contacta al administrador del sistema.");
        }

        LocalDate hoy = LocalDate.now();

        // Fecha null = sin límite, no se bloquea
        if (Boolean.TRUE.equals(farmacia.getEnPeriodoPrueba())) {
            if (farmacia.getPruebaHasta() != null && farmacia.getPruebaHasta().isBefore(hoy)) {
                throw new SuscripcionVencidaException("Tu periodo de prueba ha vencido. Contacta al administrador del sistema.");
            }
        } else {
            if (farmacia.getSuscripcionVigencia() != null && farmacia.getSuscripcionVigencia().isBefore(hoy)) {
                throw new SuscripcionVencidaException("Tu suscripción ha vencido. Contacta al administrador del sistema.");
            }
        }
    }
}
