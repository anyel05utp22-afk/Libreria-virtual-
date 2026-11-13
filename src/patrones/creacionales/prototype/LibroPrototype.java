package patrones.creacionales.prototype;

public class LibroPrototype implements IPublicacionPrototype {
    private String autor;
    private String titulo;
    private String editorial;
    private String materia;
    private double precio;
    private int stock;

    public LibroPrototype(String autor, String titulo, String editorial, String materia, double precio, int stock) {
        this.autor = autor;
        this.titulo = titulo;
        this.editorial = editorial;
        this.materia = materia;
        this.precio = precio;
        this.stock = stock;
    }

    @Override
    public IPublicacionPrototype clonar() {
        return new LibroPrototype(this.autor, this.titulo + " (Copia)", this.editorial, this.materia, this.precio, this.stock);
    }

    @Override
    public String getInfo() {
        return String.format("[Prototipo] %s por %s (Editorial: %s) - S/ %.2f", titulo, autor, editorial, precio);
    }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }
    public String getMateria() { return materia; }
    public void setMateria(String materia) { this.materia = materia; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}

