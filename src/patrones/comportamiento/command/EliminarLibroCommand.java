package patrones.comportamiento.command;

import patrones.estructurales.facade.LibreriaFacade;
import patrones.solid.srp.LibroSRP;

public class EliminarLibroCommand implements ICommand {
    private final LibreriaFacade facade;
    private final LibroSRP libroCopia;

    public EliminarLibroCommand(LibreriaFacade facade, LibroSRP libroCopia) {
        this.facade = facade;
        this.libroCopia = libroCopia;
    }

    @Override
    public boolean ejecutar() {
        return facade.eliminarLibro(libroCopia.getId());
    }

    @Override
    public boolean deshacer() {
        // Restaurar registro
        return facade.registrarLibro(
            libroCopia.getAutor(),
            libroCopia.getTitulo(),
            libroCopia.getEditorial(),
            libroCopia.getMateria(),
            libroCopia.getPrecio(),
            libroCopia.getStock()
        );
    }

    @Override
    public String getDescripcion() {
        return "Eliminar ID #" + libroCopia.getId() + " (" + libroCopia.getTitulo() + ")";
    }
}

