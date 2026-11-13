package patrones.estructurales.composite;

/**
 * SEMANA 9: Patrón Estructural Composite
 * Permite componer objetos en estructuras de árbol para representar jerarquías parte-todo.
 */
public interface ICatalogoComponent {
    String getNombre();
    void mostrarJerarquia(String prefijo);
    int contarItems();
}

