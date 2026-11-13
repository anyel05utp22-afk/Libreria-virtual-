package patrones.creacionales.abstractfactory;

import patrones.solid.lsp.LibroLSP;
import patrones.solid.lsp.RevistaLSP;

/**
 * SEMANA 7: Patrón Creacional Abstract Factory
 * Proporciona una interfaz para crear familias de objetos relacionados
 * sin especificar sus clases concretas.
 */
public interface EditorialAbstractFactory {
    LibroLSP crearLibroDestacado();
    RevistaLSP crearRevistaDestacada();
    String getNombreEditorial();
}

