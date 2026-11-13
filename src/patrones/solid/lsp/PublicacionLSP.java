package patrones.solid.lsp;

/**
 * SEMANA 2: Principio de Sustitución de Liskov (LSP)
 * Toda subclase debe poder sustituir a la clase base sin alterar la corrección del programa.
 */
public abstract class PublicacionLSP {
    protected int id;
    protected String titulo;
    protected double precio;
    protected int stock;

    public PublicacionLSP(int id, String titulo, double precio, int stock) {
        this.id = id;
        this.titulo = titulo;
        this.precio = precio;
        this.stock = stock;
    }

    public abstract String getTipo();
    public abstract String getFichaTecnica();

    public boolean hayStock() {
        return stock > 0;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
}

