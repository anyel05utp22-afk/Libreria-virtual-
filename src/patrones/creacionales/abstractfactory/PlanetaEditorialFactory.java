package patrones.creacionales.abstractfactory;

import patrones.solid.lsp.LibroLSP;
import patrones.solid.lsp.RevistaLSP;

public class PlanetaEditorialFactory implements EditorialAbstractFactory {
    @Override
    public LibroLSP crearLibroDestacado() {
        return new LibroLSP(101, "Sapiens: De animales a dioses", "Yuval Noah Harari", "Planeta", "Historia", 65.00, 15);
    }

    @Override
    public RevistaLSP crearRevistaDestacada() {
        return new RevistaLSP(201, "National Geographic Edición Especial", "Planeta / RBA", 12, "2024-06-01", 28.00, 20);
    }

    @Override
    public String getNombreEditorial() {
        return "Grupo Planeta";
    }
}

