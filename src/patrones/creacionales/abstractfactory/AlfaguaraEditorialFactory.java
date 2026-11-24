package patrones.creacionales.abstractfactory;

import patrones.solid.lsp.LibroLSP;
import patrones.solid.lsp.RevistaLSP;

/**
 * SEMANA 7: Abstract Factory - Familia concreta para Editorial Alfaguara.
 * Genera productos coherentes (Libro + Revista literaria) de la misma casa editorial.
 */
public class AlfaguaraEditorialFactory implements EditorialAbstractFactory {
    @Override
    public LibroLSP crearLibroDestacado() {
        return new LibroLSP(103, "Rayuela (Edición Conmemorativa)", "Julio Cortázar",
            "Alfaguara", "Clásicos", 56.00, 12);
    }

    @Override
    public RevistaLSP crearRevistaDestacada() {
        return new RevistaLSP(203, "Cuadernos Hispanoamericanos", "Alfaguara Cultura",
            48, "2026-03-01", 24.00, 18);
    }

    @Override
    public String getNombreEditorial() {
        return "Editorial Alfaguara";
    }
}
