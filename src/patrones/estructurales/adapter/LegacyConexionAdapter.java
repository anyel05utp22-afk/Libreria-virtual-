package patrones.estructurales.adapter;

import patrones.solid.lsp.CuentoLSP;
import patrones.solid.lsp.RevistaLSP;
import patrones.solid.srp.LibroSRP;
import proyectopoo.ConexionCuento;
import proyectopoo.ConexionLibro;
import proyectopoo.ConexionRevista;

/**
 * SEMANA 8: Patrón Estructural Adapter
 * Adapta las clases heredadas de acceso JDBC (ConexionLibro, ConexionCuento, ConexionRevista
 * que invocan los procedimientos almacenados CRL, CRC, CRR y consultas legacy) a la interfaz
 * moderna IServicioPublicacion utilizada por el repositorio DIP y la fachada web.
 */
public class LegacyConexionAdapter implements IServicioPublicacion {
    private final ConexionLibro legacyConexionLibro;
    private ConexionCuento legacyConexionCuento;
    private ConexionRevista legacyConexionRevista;

    public LegacyConexionAdapter() {
        this.legacyConexionLibro = new ConexionLibro();
    }

    private synchronized ConexionCuento getConexionCuento() {
        if (legacyConexionCuento == null) {
            legacyConexionCuento = new ConexionCuento();
        }
        return legacyConexionCuento;
    }

    private synchronized ConexionRevista getConexionRevista() {
        if (legacyConexionRevista == null) {
            legacyConexionRevista = new ConexionRevista();
        }
        return legacyConexionRevista;
    }

    @Override
    public boolean insertar(LibroSRP libro) {
        int res = legacyConexionLibro.Rlibro(
            libro.getAutor(),
            libro.getTitulo(),
            libro.getEditorial(),
            libro.getMateria(),
            (float) libro.getPrecio(),
            libro.getStock()
        );
        return res >= 1;
    }

    @Override
    public boolean actualizar(LibroSRP libro) {
        int res = legacyConexionLibro.Mlibro(
            libro.getId(),
            libro.getAutor(),
            libro.getTitulo(),
            libro.getEditorial(),
            libro.getMateria(),
            (float) libro.getPrecio(),
            libro.getStock()
        );
        return res >= 1;
    }

    @Override
    public boolean suprimir(int id) {
        int res = legacyConexionLibro.Elibro(id);
        return res >= 1;
    }

    public boolean insertarCuento(CuentoLSP cuento) {
        int res = getConexionCuento().Rcuento(
            cuento.getAutor(),
            cuento.getTitulo(),
            cuento.getEditorial(),
            cuento.getEdadRecomendada(),
            cuento.getNumeroPaginas(),
            (float) cuento.getPrecio(),
            cuento.getStock()
        );
        return res >= 1;
    }

    public boolean insertarRevista(RevistaLSP revista) {
        int res = getConexionRevista().Rrevista(
            revista.getMarca(),
            revista.getTitulo(),
            revista.getEdicion(),
            revista.getFechaPublicacion(),
            revista.getPrecio(),
            revista.getStock()
        );
        return res >= 1;
    }
}
