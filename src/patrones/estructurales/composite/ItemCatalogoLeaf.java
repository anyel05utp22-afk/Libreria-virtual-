package patrones.estructurales.composite;

import java.util.Locale;

/**
 * SEMANA 9: Composite - Nodo Hoja (ItemCatalogoLeaf)
 * Representa un libro o publicación individual dentro del árbol jerárquico del catálogo.
 */
public class ItemCatalogoLeaf implements ICatalogoComponent {
    private final int id;
    private final String titulo;
    private final String autor;
    private final double precio;
    private final int stock;

    public ItemCatalogoLeaf(String titulo, double precio) {
        this(0, titulo, "", precio, 1);
    }

    public ItemCatalogoLeaf(int id, String titulo, String autor, double precio, int stock) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor == null ? "" : autor;
        this.precio = precio;
        this.stock = Math.max(0, stock);
    }

    @Override
    public String getNombre() {
        return titulo;
    }

    @Override
    public void mostrarJerarquia(String prefijo) {
        System.out.println(prefijo + "- [Item] " + titulo + " (S/ " + precio + ", stock: " + stock + ")");
    }

    @Override
    public int contarItems() {
        return 1;
    }

    @Override
    public int contarStockTotal() {
        return stock;
    }

    @Override
    public double calcularValorInventario() {
        return precio * stock;
    }

    @Override
    public String toJson() {
        return String.format(Locale.US,
            "{\"tipo\":\"item\",\"id\":%d,\"nombre\":\"%s\",\"autor\":\"%s\",\"precio\":%.2f,\"stock\":%d,\"valorInventario\":%.2f}",
            id,
            escapar(titulo),
            escapar(autor),
            precio,
            stock,
            calcularValorInventario()
        );
    }

    private static String escapar(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
