package patrones.grasp.fabricacionpura;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SEMANA 17: Patrón GRASP - Fabricación Pura (Pure Fabrication)
 * Servicio artificial que no pertenece al dominio editorial (Libro/Cliente),
 * creado para centralizar la auditoría y trazabilidad con alta cohesión y bajo acoplamiento.
 */
public class AuditoriaTransaccionesService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final int MAX_LOGS = 80;
    private static final List<String> LOGS_EN_MEMORIA = new CopyOnWriteArrayList<>();
    private static final AtomicInteger TOTAL_EVENTOS = new AtomicInteger(0);

    public static void registrarLog(String operacion, String usuario, String detalles) {
        registrarConPatron("SISTEMA", operacion, usuario, detalles);
    }

    public static void registrarConPatron(String patron, String operacion, String usuario, String detalles) {
        String hora = LocalDateTime.now().format(FORMATTER);
        String entrada = String.format("[%s] [%s] Usuario: %s | Op: %s | %s",
            hora, patron, usuario, operacion, detalles);
        LOGS_EN_MEMORIA.add(0, entrada);
        TOTAL_EVENTOS.incrementAndGet();
        while (LOGS_EN_MEMORIA.size() > MAX_LOGS) {
            LOGS_EN_MEMORIA.remove(LOGS_EN_MEMORIA.size() - 1);
        }
        System.out.println("[AUDITORIA] " + entrada);
    }

    public static List<String> obtenerLogs() {
        return Collections.unmodifiableList(new ArrayList<>(LOGS_EN_MEMORIA));
    }

    public static int getTotalEventos() {
        return TOTAL_EVENTOS.get();
    }
}
