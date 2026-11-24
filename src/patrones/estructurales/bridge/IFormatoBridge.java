package patrones.estructurales.bridge;

/**
 * SEMANA 11: Patrón Estructural Bridge (Implementador)
 * Desacopla la abstracción de una publicación (Libro, Revista, Cuento)
 * de su formato/medio de entrega (Impreso Físico, E-Book Digital, Audiolibro HD).
 */
public interface IFormatoBridge {
    String renderizarContenido(String titulo, String autor);
    double costoAdicionalFormato();

    default double ajustarPrecio(double precioBase) {
        return Math.max(0.0, precioBase + costoAdicionalFormato());
    }

    default boolean consumeStockFisico() {
        return true;
    }

    default String getNombreFormato() {
        return getClass().getSimpleName();
    }
}
