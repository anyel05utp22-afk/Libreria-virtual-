package patrones.grasp.controlador;

import java.util.List;
import patrones.estructurales.facade.LibreriaFacade;
import patrones.solid.srp.LibroSRP;

/**
 * SEMANA 16: Patrón GRASP - Controlador (Controller)
 * Primer objeto más allá de la capa de interfaz gráfica que recibe y coordina
 * las operaciones del sistema. Fomenta el bajo acoplamiento y la alta cohesión.
 */
public class LibreriaGRASPController {
    private final LibreriaFacade facade;

    public LibreriaGRASPController() {
        this.facade = new LibreriaFacade();
    }

    public List<LibroSRP> procesarListadoCatalogo() {
        return facade.listarLibros();
    }

    public boolean procesarRegistro(String autor, String titulo, String editorial, String materia, double precio, int stock) {
        if (autor == null || titulo == null || precio < 0 || stock < 0) {
            return false;
        }
        return facade.registrarLibro(autor, titulo, editorial, materia, precio, stock);
    }

    public boolean procesarModificacion(int id, String autor, String titulo, String editorial, String materia, double precio, int stock) {
        if (id <= 0) return false;
        return facade.actualizarLibro(id, autor, titulo, editorial, materia, precio, stock);
    }

    public boolean procesarEliminacion(int id) {
        if (id <= 0) return false;
        return facade.eliminarLibro(id);
    }
}

