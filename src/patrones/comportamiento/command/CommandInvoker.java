package patrones.comportamiento.command;

import java.util.Stack;

public class CommandInvoker {
    private final Stack<ICommand> historialDeshacer = new Stack<>();
    private final Stack<ICommand> historialRehacer = new Stack<>();

    public boolean ejecutarComando(ICommand cmd) {
        boolean ok = cmd.ejecutar();
        if (ok) {
            historialDeshacer.push(cmd);
            historialRehacer.clear();
            System.out.println("[Command Ejecutado] " + cmd.getDescripcion());
        }
        return ok;
    }

    public boolean deshacer() {
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

    public boolean rehacer() {
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
}

