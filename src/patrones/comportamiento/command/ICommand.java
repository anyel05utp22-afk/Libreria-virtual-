package patrones.comportamiento.command;

/**
 * SEMANA 13: Patrón de Comportamiento Command
 * Encapsula una petición como un objeto, permitiendo parametrizar operaciones,
 * poner peticiones en cola y soportar operaciones que se pueden deshacer (Undo).
 */
public interface ICommand {
    boolean ejecutar();
    boolean deshacer();
    String getDescripcion();
}

