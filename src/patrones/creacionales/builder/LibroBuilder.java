package patrones.creacionales.builder;

import patrones.solid.srp.LibroSRP;

/**
 * SEMANA 7: Patrón Creacional Builder
 * Separa la construcción de un objeto complejo de su representación,
 * permitiendo que el mismo proceso de construcción pueda crear diferentes representaciones.
 */
public class LibroBuilder {
    private int id;
    private String autor = "Anónimo";
    private String titulo = "Sin Título";
    private String editorial = "Independiente";
    private String materia = "Literatura";
    private double precio = 0.0;
    private int stock = 0;

    public LibroBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public LibroBuilder setAutor(String autor) {
        this.autor = autor;
        return this;
    }

    public LibroBuilder setTitulo(String titulo) {
        this.titulo = titulo;
        return this;
    }

    public LibroBuilder setEditorial(String editorial) {
        this.editorial = editorial;
        return this;
    }

    public LibroBuilder setMateria(String materia) {
        this.materia = materia;
        return this;
    }

    public LibroBuilder setPrecio(double precio) {
        this.precio = precio;
        return this;
    }

    public LibroBuilder setStock(int stock) {
        this.stock = stock;
        return this;
    }

    public LibroSRP build() {
        return new LibroSRP(id, autor, titulo, editorial, materia, precio, stock);
    }
}

