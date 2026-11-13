package patrones.estructurales.composite;

public class ItemCatalogoLeaf implements ICatalogoComponent {
    private String titulo;
    private double precio;

    public ItemCatalogoLeaf(String titulo, double precio) {
        this.titulo = titulo;
        this.precio = precio;
    }

    @Override
    public String getNombre() {
        return titulo;
    }

    @Override
    public void mostrarJerarquia(String prefijo) {
        System.out.println(prefijo + "- [Item] " + titulo + " (S/ " + precio + ")");
    }

    @Override
    public int contarItems() {
        return 1;
    }
}

