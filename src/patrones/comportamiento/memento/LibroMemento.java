package patrones.comportamiento.memento;

/**
 * SEMANA 13: Patrón de Comportamiento Memento
 * Captura y externaliza el estado interno de un objeto sin violar la encapsulación,
 * permitiendo restaurar el objeto a este estado más adelante.
 */
public class LibroMemento {
    private final String autor;
    private final String titulo;
    private final String editorial;
    private final String materia;
    private final double precio;
    private final int stock;
    private final long timestamp;

    public LibroMemento(String autor, String titulo, String editorial, String materia, double precio, int stock) {
        this.autor = autor;
        this.titulo = titulo;
        this.editorial = editorial;
        this.materia = materia;
        this.precio = precio;
        this.stock = stock;
        this.timestamp = System.currentTimeMillis();
    }

    public String getAutor() { return autor; }
    public String getTitulo() { return titulo; }
    public String getEditorial() { return editorial; }
    public String getMateria() { return materia; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
    public long getTimestamp() { return timestamp; }
}

