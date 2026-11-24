package patrones.comportamiento.observer;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SEMANA 12: Patrón de Comportamiento Observer (Sujeto Observable)
 * Gestiona suscripciones globales y por título de libro; cuando el stock se repone
 * o un ejemplar es devuelto, notifica automáticamente a todos los observadores suscritos
 * y registra los avisos despachados para visualización en vivo en la plataforma web.
 */
public class StockObservable {
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final List<IObserverCliente> observadoresGlobales = new CopyOnWriteArrayList<>();
    private final Map<String, List<IObserverCliente>> observadoresPorLibro = new ConcurrentHashMap<>();
    private final List<String> notificacionesEnviadas = new CopyOnWriteArrayList<>();

    public void suscribir(IObserverCliente obs) {
        if (obs != null && !observadoresGlobales.contains(obs)) {
            observadoresGlobales.add(obs);
            System.out.println("[Observer] Cliente suscrito globalmente: " + obs.getNombreCliente());
        }
    }

    public void suscribirParaLibro(String tituloLibro, IObserverCliente obs) {
        if (obs == null) return;
        if (tituloLibro == null || tituloLibro.isBlank()) {
            suscribir(obs);
            return;
        }
        String clave = tituloLibro.trim().toLowerCase();
        List<IObserverCliente> lista = observadoresPorLibro.computeIfAbsent(clave, k -> new CopyOnWriteArrayList<>());
        boolean existe = lista.stream().anyMatch(o -> o.getNombreCliente().equalsIgnoreCase(obs.getNombreCliente()));
        if (!existe) {
            lista.add(obs);
            System.out.println("[Observer] Cliente '" + obs.getNombreCliente() + "' suscrito a alertas de: " + tituloLibro);
        }
    }

    public void desuscribir(IObserverCliente obs) {
        observadoresGlobales.remove(obs);
        for (List<IObserverCliente> lista : observadoresPorLibro.values()) {
            lista.remove(obs);
        }
    }

    public int notificarTodos(String tituloPublicacion, int nuevoStock) {
        if (tituloPublicacion == null || nuevoStock <= 0) return 0;
        String mensaje = String.format("¡Atención! '%s' ya tiene stock disponible (%d unidades).", tituloPublicacion, nuevoStock);
        int enviados = 0;

        for (IObserverCliente obs : observadoresGlobales) {
            obs.notificarNovedad(mensaje);
            registrarEnvio(obs.getNombreCliente(), mensaje);
            enviados++;
        }

        String clave = tituloPublicacion.trim().toLowerCase();
        List<IObserverCliente> especificos = observadoresPorLibro.get(clave);
        if (especificos != null) {
            for (IObserverCliente obs : especificos) {
                obs.notificarNovedad(mensaje);
                registrarEnvio(obs.getNombreCliente(), mensaje);
                enviados++;
            }
        }
        return enviados;
    }

    private void registrarEnvio(String destinatario, String mensaje) {
        String entrada = "[" + LocalTime.now().format(TIME_FMT) + "] Para " + destinatario + " → " + mensaje;
        notificacionesEnviadas.add(0, entrada);
        while (notificacionesEnviadas.size() > 40) {
            notificacionesEnviadas.remove(notificacionesEnviadas.size() - 1);
        }
    }

    public List<String> getNotificacionesEnviadas() {
        return Collections.unmodifiableList(new ArrayList<>(notificacionesEnviadas));
    }

    public int getTotalSuscriptores() {
        int total = observadoresGlobales.size();
        for (List<IObserverCliente> lista : observadoresPorLibro.values()) {
            total += lista.size();
        }
        return total;
    }

    public List<String> getResumenSuscripciones() {
        List<String> resumen = new ArrayList<>();
        for (Map.Entry<String, List<IObserverCliente>> e : observadoresPorLibro.entrySet()) {
            for (IObserverCliente obs : e.getValue()) {
                resumen.add(obs.getNombreCliente() + " → \"" + e.getKey() + "\"");
            }
        }
        for (IObserverCliente obs : observadoresGlobales) {
            resumen.add(obs.getNombreCliente() + " → (Todas las novedades)");
        }
        return resumen;
    }
}
