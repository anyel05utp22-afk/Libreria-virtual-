package patrones.solid.srp;

/**
 * SEMANA 1: Principio de Responsabilidad Única (SRP)
 * Entidad de dominio: únicamente representa los datos de un libro.
 */
public class LibroSRP {
    private int id;
    private String autor;
    private String titulo;
    private String editorial;
    private String materia;
    private double precio;
    private int stock;

    public LibroSRP(int id, String autor, String titulo, String editorial, String materia, double precio, int stock) {
        this.id = id;
        this.autor = autor;
        this.titulo = titulo;
        this.editorial = editorial;
        this.materia = materia;
        this.precio = precio;
        this.stock = stock;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
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

