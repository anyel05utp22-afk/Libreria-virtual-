package patrones.comportamiento.memento;

import java.util.ArrayList;
import java.util.List;

public class CaretakerHistorial {
    private final List<LibroMemento> snapshots = new ArrayList<>();

    public void agregarSnapshot(LibroMemento m) {
        snapshots.add(m);
        System.out.println("[Caretaker] Snapshot guardado en historial (Total: " + snapshots.size() + ")");
    }

    public LibroMemento obtenerUltimo() {
        if (!snapshots.isEmpty()) {
            return snapshots.remove(snapshots.size() - 1);
        }
        return null;
    }

    public int getCantidadSnapshots() {
        return snapshots.size();
    }
}

