package patrones.estructurales.composite;

/**
 * SEMANA 9: Patrón Estructural Composite
 * Permite componer objetos en estructuras de árbol (Categoría -> Subcategoría -> Libro)
 * para operar uniformemente sobre nodos compuestos y hojas individuales.
 */
public interface ICatalogoComponent {
    String getNombre();
    void mostrarJerarquia(String prefijo);
    int contarItems();

    default int contarStockTotal() {
        return 0;
    }

    default double calcularValorInventario() {
        return 0.0;
    }

    default String toJson() {
        return "{}";
    }
}
