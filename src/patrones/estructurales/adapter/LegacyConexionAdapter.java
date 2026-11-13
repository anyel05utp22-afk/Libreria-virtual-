package patrones.estructurales.adapter;

import proyectopoo.ConexionLibro;
import patrones.solid.srp.LibroSRP;

/**
 * SEMANA 8: Patrón Estructural Adapter
 * Convierte la interfaz de la clase existente ConexionLibro
 * en la interfaz moderna que espera el sistema.
 */
public class LegacyConexionAdapter implements IServicioPublicacion {
    private final ConexionLibro legacyConexion;

    public LegacyConexionAdapter() {
        this.legacyConexion = new ConexionLibro();
    }

    @Override
    public boolean insertar(LibroSRP libro) {
        int res = legacyConexion.Rlibro(
            libro.getAutor(),
            libro.getTitulo(),
            libro.getEditorial(),
            libro.getMateria(),
            (float) libro.getPrecio(),
            libro.getStock()
        );
        return res == 1;
    }

    @Override
    public boolean actualizar(LibroSRP libro) {
        int res = legacyConexion.Mlibro(
            libro.getId(),
            libro.getAutor(),
            libro.getTitulo(),
            libro.getEditorial(),
            libro.getMateria(),
            (float) libro.getPrecio(),
            libro.getStock()
        );
        return res == 1;
    }

    @Override
    public boolean suprimir(int id) {
        int res = legacyConexion.Elibro(id);
        return res == 1;
    }
}

