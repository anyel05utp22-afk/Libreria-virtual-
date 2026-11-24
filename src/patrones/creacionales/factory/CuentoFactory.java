package patrones.creacionales.factory;

import patrones.solid.lsp.CuentoLSP;
import patrones.solid.lsp.PublicacionLSP;

/**
 * SEMANA 7: Factory Method - Creador concreto para instancias de CuentoLSP.
 */
public class CuentoFactory extends PublicacionFactory {
    @Override
    public PublicacionLSP crearPublicacion(int id, String titulo, double precio, int stock, Object... params) {
        String autor = (params.length > 0 && params[0] != null) ? params[0].toString() : "Autor Infantil";
        String editorial = (params.length > 1 && params[1] != null) ? params[1].toString() : "Planeta Kids";
        int edad = (params.length > 2 && params[2] instanceof Number) ? ((Number) params[2]).intValue() : 6;
        int paginas = (params.length > 3 && params[3] instanceof Number) ? ((Number) params[3]).intValue() : 40;
        return new CuentoLSP(id, titulo, autor, editorial, edad, paginas, precio, stock);
    }
}
