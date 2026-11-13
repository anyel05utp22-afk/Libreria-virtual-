package patrones.creacionales.abstractfactory;

import patrones.solid.lsp.LibroLSP;
import patrones.solid.lsp.RevistaLSP;

public class NormaEditorialFactory implements EditorialAbstractFactory {
    @Override
    public LibroLSP crearLibroDestacado() {
        return new LibroLSP(102, "El Caballero de la Armadura Oxidada", "Robert Fisher", "Norma", "Autoayuda", 32.00, 30);
    }

    @Override
    public RevistaLSP crearRevistaDestacada() {
        return new RevistaLSP(202, "Comic Manga Colección", "Norma Editorial", 5, "2024-05-15", 22.00, 25);
    }

    @Override
    public String getNombreEditorial() {
        return "Editorial Norma";
    }
}

