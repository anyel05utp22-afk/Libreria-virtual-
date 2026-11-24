package patrones.creacionales.builder;

import patrones.solid.srp.LibroSRP;

/**
 * SEMANA 7: Patrón Builder - Director (DirectorPublicacion)
 * Define recetas de construcción reutilizables sobre LibroBuilder para crear
 * ediciones específicas (Coleccionista, Bolsillo, Manual Académico) de manera uniforme.
 */
public class DirectorPublicacion {

    public LibroSRP construirEdicionColeccionista(LibroBuilder builder, String titulo, String autor,
                                                  String editorial, String materia, double precioBase, int stock) {
        return builder
            .setTitulo(titulo)
            .setAutor(autor)
            .setEditorial(editorial)
            .setMateria(materia)
            .setPrecio(precioBase)
            .setStock(stock)
            .setTapaDura(true)
            .setConPrologo(true)
            .setIlustrado(true)
            .setIsbn("978-612-COL-" + (100 + Math.abs(titulo.hashCode() % 899)))
            .build();
    }

    public LibroSRP construirEdicionBolsillo(LibroBuilder builder, String titulo, String autor,
                                             String editorial, String materia, double precioBase, int stock) {
        return builder
            .setTitulo(titulo)
            .setAutor(autor)
            .setEditorial(editorial + " Bolsillo")
            .setMateria(materia)
            .setPrecio(Math.max(15.0, precioBase * 0.75))
            .setStock(stock)
            .setTapaDura(false)
            .setConPrologo(false)
            .setIlustrado(false)
            .build();
    }

    public LibroSRP construirManualAcademico(LibroBuilder builder, String titulo, String autor,
                                             String editorial, double precioBase, int stock) {
        return builder
            .setTitulo(titulo)
            .setAutor(autor)
            .setEditorial(editorial)
            .setMateria("Filosofía, Educación, Ciencia y Tecnología")
            .setPrecio(precioBase)
            .setStock(stock)
            .setTapaDura(true)
            .setConPrologo(true)
            .setIlustrado(false)
            .setIsbn("978-612-ACA-" + (100 + Math.abs(titulo.hashCode() % 899)))
            .build();
    }
}
