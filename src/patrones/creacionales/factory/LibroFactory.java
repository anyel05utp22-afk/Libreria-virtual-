package patrones.creacionales.factory;

import patrones.solid.lsp.LibroLSP;
import patrones.solid.lsp.PublicacionLSP;

public class LibroFactory extends PublicacionFactory {
    @Override
    public PublicacionLSP crearPublicacion(int id, String titulo, double precio, int stock, Object... params) {
        String autor = (params.length > 0 && params[0] != null) ? params[0].toString() : "Autor Desconocido";
        String editorial = (params.length > 1 && params[1] != null) ? params[1].toString() : "Editorial General";
        String materia = (params.length > 2 && params[2] != null) ? params[2].toString() : "General";
        return new LibroLSP(id, titulo, autor, editorial, materia, precio, stock);
    }
}

