package patrones.comportamiento.command;

import patrones.estructurales.facade.LibreriaFacade;
import patrones.solid.srp.LibroSRP;

/**
 * SEMANA 13: Command - Encapsula el registro de un libro y captura su ID real
 * para que deshacer() elimine exactamente el registro recién insertado.
 */
public class RegistrarLibroCommand implements ICommand {
    private final LibreriaFacade facade;
    private final LibroSRP libro;
    private int idGenerado = -1;

    public RegistrarLibroCommand(LibreriaFacade facade, LibroSRP libro) {
        this.facade = facade;
        this.libro = libro;
    }

    @Override
    public boolean ejecutar() {
        int id = facade.registrarLibroRetornandoId(
            libro.getAutor(),
            libro.getTitulo(),
            libro.getEditorial(),
            libro.getMateria(),
            libro.getPrecio(),
            libro.getStock()
        );
        if (id > 0) {
            this.idGenerado = id;
            this.libro.setId(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean deshacer() {
        if (idGenerado > 0) {
            return facade.eliminarLibro(idGenerado);
        }
        return false;
    }

    public int getIdGenerado() {
        return idGenerado;
    }

    @Override
    public String getDescripcion() {
        return "Registrar: " + libro.getTitulo() + " (" + libro.getAutor() + ")";
    }
}
