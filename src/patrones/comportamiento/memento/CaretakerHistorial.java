package patrones.comportamiento.memento;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SEMANA 13: Patrón de Comportamiento Memento (Caretaker / Custodio)
 * Administra el historial de snapshots del formulario evitando duplicados consecutivos
 * y permitiendo restaurar el último estado distinto o elegir cualquier instantánea previa.
 */
public class CaretakerHistorial {
    private static final int MAX_SNAPSHOTS = 20;
    private final List<LibroMemento> snapshots = new ArrayList<>();

    public synchronized boolean agregarSnapshot(LibroMemento m) {
        if (m == null) return false;
        if (!snapshots.isEmpty()) {
            LibroMemento ultimo = snapshots.get(snapshots.size() - 1);
            if (esIgual(ultimo, m)) {
                return false; // Evita guardar duplicados idénticos seguidos
            }
        }
        snapshots.add(m);
        if (snapshots.size() > MAX_SNAPSHOTS) {
            snapshots.remove(0);
        }
        System.out.println("[Caretaker] Snapshot guardado en historial (Total: " + snapshots.size() + ")");
        return true;
    }

    public synchronized LibroMemento obtenerUltimo() {
        if (!snapshots.isEmpty()) {
            return snapshots.remove(snapshots.size() - 1);
        }
        return null;
    }

    public synchronized LibroMemento obtenerPorIndice(int idx) {
        if (idx >= 0 && idx < snapshots.size()) {
            return snapshots.get(idx);
        }
        return null;
    }

    public synchronized List<LibroMemento> listarSnapshots() {
        return Collections.unmodifiableList(new ArrayList<>(snapshots));
    }

    public synchronized int getCantidadSnapshots() {
        return snapshots.size();
    }

    private boolean esIgual(LibroMemento a, LibroMemento b) {
        return safe(a.getAutor()).equals(safe(b.getAutor()))
            && safe(a.getTitulo()).equals(safe(b.getTitulo()))
            && safe(a.getEditorial()).equals(safe(b.getEditorial()))
            && safe(a.getMateria()).equals(safe(b.getMateria()))
            && Double.compare(a.getPrecio(), b.getPrecio()) == 0
            && a.getStock() == b.getStock();
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
