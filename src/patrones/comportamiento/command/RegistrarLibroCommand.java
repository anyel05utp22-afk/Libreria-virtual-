package patrones.comportamiento.command;

import patrones.estructurales.facade.LibreriaFacade;
import patrones.solid.srp.LibroSRP;

public class RegistrarLibroCommand implements ICommand {
    private final LibreriaFacade facade;
    private final LibroSRP libro;
    private int idGenerado;

    public RegistrarLibroCommand(LibreriaFacade facade, LibroSRP libro) {
        this.facade = facade;
        this.libro = libro;
    }

    @Override
    public boolean ejecutar() {
        boolean ok = facade.registrarLibro(
            libro.getAutor(),
            libro.getTitulo(),
            libro.getEditorial(),
            libro.getMateria(),
            libro.getPrecio(),
            libro.getStock()
        );
        return ok;
    }

    @Override
    public boolean deshacer() {
        if (idGenerado > 0) {
            return facade.eliminarLibro(idGenerado);
        }
        return false;
    }

    @Override
    public String getDescripcion() {
        return "Registrar: " + libro.getTitulo() + " (" + libro.getAutor() + ")";
    }
}

