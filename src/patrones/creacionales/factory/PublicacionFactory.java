package patrones.creacionales.factory;

import patrones.solid.lsp.PublicacionLSP;

/**
 * SEMANA 7: Patrón Creacional Factory Method
 * Define una interfaz para crear un objeto, permitiendo a las subclases decidir qué clase instanciar.
 */
public abstract class PublicacionFactory {
    public abstract PublicacionLSP crearPublicacion(int id, String titulo, double precio, int stock, Object... params);

    public void registrarEnInventario(int id, String titulo, double precio, int stock, Object... params) {
        PublicacionLSP pub = crearPublicacion(id, titulo, precio, stock, params);
        System.out.println("[Factory Method] Creado: " + pub.getFichaTecnica());
    }
}

