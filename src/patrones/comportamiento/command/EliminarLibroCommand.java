package patrones.comportamiento.command;

import patrones.estructurales.facade.LibreriaFacade;
import patrones.solid.srp.LibroSRP;

/**
 * SEMANA 13: Command - Encapsula la eliminación de un libro conservando su copia real
 * para restaurarlo fielmente al ejecutar deshacer() (Undo) y re-eliminarlo en rehacer() (Redo).
 */
public class EliminarLibroCommand implements ICommand {
    private final LibreriaFacade facade;
    private final LibroSRP libroCopia;
    private int idActual;

    public EliminarLibroCommand(LibreriaFacade facade, LibroSRP libroCopia) {
        this.facade = facade;
        this.libroCopia = libroCopia;
        this.idActual = libroCopia.getId();
    }

    @Override
    public boolean ejecutar() {
        return facade.eliminarLibro(idActual);
    }

    @Override
    public boolean deshacer() {
        int nuevoId = facade.registrarLibroRetornandoId(
            libroCopia.getAutor(),
            libroCopia.getTitulo(),
            libroCopia.getEditorial(),
            libroCopia.getMateria(),
            libroCopia.getPrecio(),
            libroCopia.getStock()
        );
        if (nuevoId > 0) {
            this.idActual = nuevoId;
            return true;
        }
        return false;
    }

    @Override
    public String getDescripcion() {
        return "Eliminar #" + idActual + " (" + libroCopia.getTitulo() + ")";
    }
}
