package patrones.comportamiento.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * SEMANA 13: Patrón de Comportamiento Command (Invocador con pilas Undo / Redo)
 */
public class CommandInvoker {
    private final Stack<ICommand> historialDeshacer = new Stack<>();
    private final Stack<ICommand> historialRehacer = new Stack<>();

    public synchronized boolean ejecutarComando(ICommand cmd) {
        boolean ok = cmd.ejecutar();
        if (ok) {
            historialDeshacer.push(cmd);
            historialRehacer.clear();
            System.out.println("[Command Ejecutado] " + cmd.getDescripcion());
        }
        return ok;
    }

    public synchronized boolean deshacer() {
        if (!historialDeshacer.isEmpty()) {
            ICommand cmd = historialDeshacer.pop();
            boolean ok = cmd.deshacer();
            if (ok) {
                historialRehacer.push(cmd);
                System.out.println("[Command Deshecho (Undo)] " + cmd.getDescripcion());
                return true;
            }
        }
        return false;
    }

    public synchronized boolean rehacer() {
        if (!historialRehacer.isEmpty()) {
            ICommand cmd = historialRehacer.pop();
            boolean ok = cmd.ejecutar();
            if (ok) {
                historialDeshacer.push(cmd);
                System.out.println("[Command Rehecho (Redo)] " + cmd.getDescripcion());
                return true;
            }
        }
        return false;
    }

    public synchronized boolean puedeDeshacer() { return !historialDeshacer.isEmpty(); }
    public synchronized boolean puedeRehacer() { return !historialRehacer.isEmpty(); }
    public synchronized int getTamanoUndo() { return historialDeshacer.size(); }
    public synchronized int getTamanoRedo() { return historialRehacer.size(); }

    public synchronized String getProximaDescripcionUndo() {
        return historialDeshacer.isEmpty() ? "" : historialDeshacer.peek().getDescripcion();
    }

    public synchronized String getProximaDescripcionRedo() {
        return historialRehacer.isEmpty() ? "" : historialRehacer.peek().getDescripcion();
    }

    public synchronized List<String> getHistorialUndo() {
        List<String> lista = new ArrayList<>();
        for (int i = historialDeshacer.size() - 1; i >= 0; i--) {
            lista.add(historialDeshacer.get(i).getDescripcion());
        }
        return lista;
    }
}
