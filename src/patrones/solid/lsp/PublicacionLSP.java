package patrones.solid.lsp;

/**
 * SEMANA 2: Principio de Sustitución de Liskov (LSP)
 * Toda subclase (LibroLSP, CuentoLSP, RevistaLSP) puede sustituir a PublicacionLSP
 * respetando sus precondiciones, postcondiciones e invariantes sin lanzar excepciones inesperadas.
 */
public abstract class PublicacionLSP {
    protected int id;
    protected String titulo;
    protected double precio;
    protected int stock;

    public PublicacionLSP(int id, String titulo, double precio, int stock) {
        this.id = id;
        this.titulo = titulo;
        this.precio = Math.max(0.0, precio);
        this.stock = Math.max(0, stock);
    }

    public abstract String getTipo();
    public abstract String getFichaTecnica();

    public boolean hayStock() {
        return stock > 0;
    }

    public double calcularPrecioConIGV() {
        return precio * 1.18;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = Math.max(0, stock); }
}
