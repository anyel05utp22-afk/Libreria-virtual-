package patrones.comportamiento.command;

import patrones.estructurales.facade.LibreriaFacade;
import patrones.solid.srp.LibroSRP;

/**
 * SEMANA 13: Command - Encapsula la modificación de un libro guardando el estado
 * anterior y el estado nuevo, permitiendo Undo/Redo exactos sobre los datos modificados.
 */
public class ModificarLibroCommand implements ICommand {
    private final LibreriaFacade facade;
    private final LibroSRP anterior;
    private final LibroSRP nuevo;

    public ModificarLibroCommand(LibreriaFacade facade, LibroSRP anterior, LibroSRP nuevo) {
        this.facade = facade;
        this.anterior = anterior;
        this.nuevo = nuevo;
    }

    @Override
    public boolean ejecutar() {
        return facade.actualizarLibro(
            nuevo.getId(),
            nuevo.getAutor(),
            nuevo.getTitulo(),
            nuevo.getEditorial(),
            nuevo.getMateria(),
            nuevo.getPrecio(),
            nuevo.getStock()
        );
    }

    @Override
    public boolean deshacer() {
        return facade.actualizarLibro(
            anterior.getId(),
            anterior.getAutor(),
            anterior.getTitulo(),
            anterior.getEditorial(),
            anterior.getMateria(),
            anterior.getPrecio(),
            anterior.getStock()
        );
    }

    @Override
    public String getDescripcion() {
        return "Modificar #" + nuevo.getId() + " (" + nuevo.getTitulo() + ")";
    }
}
