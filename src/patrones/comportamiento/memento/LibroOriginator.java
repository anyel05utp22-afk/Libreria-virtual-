package patrones.comportamiento.memento;

public class LibroOriginator {
    private String autor;
    private String titulo;
    private String editorial;
    private String materia;
    private double precio;
    private int stock;

    public void setEstado(String autor, String titulo, String editorial, String materia, double precio, int stock) {
        this.autor = autor;
        this.titulo = titulo;
        this.editorial = editorial;
        this.materia = materia;
        this.precio = precio;
        this.stock = stock;
    }

    public LibroMemento guardarSnapshot() {
        return new LibroMemento(autor, titulo, editorial, materia, precio, stock);
    }

    public void restaurar(LibroMemento memento) {
        if (memento != null) {
            this.autor = memento.getAutor();
            this.titulo = memento.getTitulo();
            this.editorial = memento.getEditorial();
            this.materia = memento.getMateria();
            this.precio = memento.getPrecio();
            this.stock = memento.getStock();
            System.out.println("[Memento] Estado restaurado al snapshot de: " + memento.getTimestamp());
        }
    }

    public String getAutor() { return autor; }
    public String getTitulo() { return titulo; }
    public String getEditorial() { return editorial; }
    public String getMateria() { return materia; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
}

