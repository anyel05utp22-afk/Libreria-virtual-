package patrones.creacionales.factory;

import patrones.solid.lsp.PublicacionLSP;
import patrones.solid.lsp.RevistaLSP;

public class RevistaFactory extends PublicacionFactory {
    @Override
    public PublicacionLSP crearPublicacion(int id, String titulo, double precio, int stock, Object... params) {
        String marca = (params.length > 0 && params[0] != null) ? params[0].toString() : "Marca General";
        int edicion = (params.length > 1 && params[1] instanceof Integer) ? (Integer) params[1] : 1;
        String fecha = (params.length > 2 && params[2] != null) ? params[2].toString() : "2024-01-01";
        return new RevistaLSP(id, titulo, marca, edicion, fecha, precio, stock);
    }
}

