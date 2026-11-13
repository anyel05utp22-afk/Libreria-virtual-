package patrones.grasp.fabricacionpura;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * SEMANA 17: Patrón GRASP - Fabricación Pura (Pure Fabrication)
 * Clase ficticia inventada que no representa un concepto directo del dominio real (no es Libro ni Cliente),
 * creada específicamente para mantener alta cohesión y bajo acoplamiento en auditoría y logs.
 */
public class AuditoriaTransaccionesService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void registrarLog(String operacion, String usuario, String detalles) {
        String fecha = LocalDateTime.now().format(FORMATTER);
        System.out.printf("[AUDITORIA - %s] Usuario: %s | Operacion: %s | Detalles: %s%n",
            fecha, usuario, operacion, detalles);
    }
}
