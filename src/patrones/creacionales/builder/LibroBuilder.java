package patrones.creacionales.builder;

import patrones.solid.srp.LibroSRP;

/**
 * SEMANA 7: Patrón Creacional Builder
 * Separa la construcción paso a paso de una publicación compleja de su representación final,
 * soportando atributos opcionales como ISBN, tapa dura, prólogo, ilustraciones y páginas.
 */
public class LibroBuilder {
    private int id;
    private String autor = "Anónimo";
    private String titulo = "Sin Título";
    private String editorial = "Independiente";
    private String materia = "Clásicos";
    private double precio = 35.0;
    private int stock = 5;

    // Atributos opcionales enriquecidos paso a paso
    private String isbn = "";
    private boolean tapaDura = false;
    private boolean conPrologo = false;
    private boolean ilustrado = false;
    private int paginas = 0;

    public LibroBuilder setId(int id) { this.id = id; return this; }
    public LibroBuilder setAutor(String autor) {
        if (autor != null && !autor.isBlank()) this.autor = autor.trim();
        return this;
    }
    public LibroBuilder setTitulo(String titulo) {
        if (titulo != null && !titulo.isBlank()) this.titulo = titulo.trim();
        return this;
    }
    public LibroBuilder setEditorial(String editorial) {
        if (editorial != null && !editorial.isBlank()) this.editorial = editorial.trim();
        return this;
    }
    public LibroBuilder setMateria(String materia) {
        if (materia != null && !materia.isBlank()) this.materia = materia.trim();
        return this;
    }
    public LibroBuilder setPrecio(double precio) { this.precio = Math.max(0.0, precio); return this; }
    public LibroBuilder setStock(int stock) { this.stock = Math.max(0, stock); return this; }

    public LibroBuilder setIsbn(String isbn) { this.isbn = isbn != null ? isbn.trim() : ""; return this; }
    public LibroBuilder setTapaDura(boolean tapaDura) { this.tapaDura = tapaDura; return this; }
    public LibroBuilder setConPrologo(boolean conPrologo) { this.conPrologo = conPrologo; return this; }
    public LibroBuilder setIlustrado(boolean ilustrado) { this.ilustrado = ilustrado; return this; }
    public LibroBuilder setPaginas(int paginas) { this.paginas = Math.max(0, paginas); return this; }

    public LibroSRP build() {
        StringBuilder editorialEnriquecida = new StringBuilder(editorial);
        if (tapaDura) editorialEnriquecida.append(" [Tapa Dura]");
        if (ilustrado) editorialEnriquecida.append(" [Ilustrado]");
        if (conPrologo) editorialEnriquecida.append(" [Con Prólogo]");
        if (!isbn.isEmpty()) editorialEnriquecida.append(" ISBN:").append(isbn);

        // Ajuste limpio de longitud para respetar VARCHAR(100) en MySQL
        String edFinal = editorialEnriquecida.toString();
        if (edFinal.length() > 98) {
            edFinal = edFinal.substring(0, 98);
        }

        double precioFinal = precio;
        if (tapaDura) precioFinal += 12.0;
        if (ilustrado) precioFinal += 8.0;

        return new LibroSRP(id, autor, titulo, edFinal, materia, precioFinal, stock);
    }

    public String getResumenConstruccion() {
        return String.format("Builder[titulo=%s, tapaDura=%s, prologo=%s, ilustrado=%s, paginas=%d, isbn=%s]",
            titulo, tapaDura, conPrologo, ilustrado, paginas, isbn.isEmpty() ? "N/A" : isbn);
    }
}
