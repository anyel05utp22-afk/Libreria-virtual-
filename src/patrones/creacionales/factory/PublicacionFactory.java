package patrones.creacionales.factory;

import patrones.solid.lsp.PublicacionLSP;

/**
 * SEMANA 7: Patrón Creacional Factory Method
 * Define el método fábrica abstracto crearPublicacion(), delegando en las subclases
 * (LibroFactory, CuentoFactory, RevistaFactory) la instanciación concreta de PublicacionLSP.
 */
public abstract class PublicacionFactory {
    public abstract PublicacionLSP crearPublicacion(int id, String titulo, double precio, int stock, Object... params);

    public PublicacionLSP registrarEnInventario(int id, String titulo, double precio, int stock, Object... params) {
        PublicacionLSP pub = crearPublicacion(id, titulo, precio, stock, params);
        System.out.println("[Factory Method] Creado (" + pub.getTipo() + "): " + pub.getFichaTecnica());
        return pub;
    }

    public static PublicacionFactory obtenerFabrica(String tipo) {
        if (tipo == null) return new LibroFactory();
        switch (tipo.trim().toLowerCase()) {
            case "cuento":
            case "cuentos":
                return new CuentoFactory();
            case "revista":
            case "revistas":
                return new RevistaFactory();
            default:
                return new LibroFactory();
        }
    }
}
