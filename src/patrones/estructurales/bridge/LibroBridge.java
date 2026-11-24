package patrones.estructurales.bridge;

/**
 * SEMANA 11: Bridge - Abstracción refinada para Libros.
 */
public class LibroBridge extends PublicacionBridge {
    private final String editorial;

    public LibroBridge(IFormatoBridge formato, String titulo, String autor, String editorial, double precioBase) {
        super(formato, titulo, autor, precioBase);
        this.editorial = editorial == null ? "Editorial General" : editorial;
    }

    @Override
    public String getDetalleEntrega() {
        return "[Libro | " + editorial + "] " + formato.renderizarContenido(titulo, autor);
    }
}
