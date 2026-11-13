package patrones.estructurales.bridge;

/**
 * SEMANA 11: Patrón Estructural Bridge (Implementador)
 * Desacopla la abstracción de su implementación permitiendo que ambas varíen independientemente.
 */
public interface IFormatoBridge {
    String renderizarContenido(String titulo, String autor);
    double costoAdicionalFormato();
}

