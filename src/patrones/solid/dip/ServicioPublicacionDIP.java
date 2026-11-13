package patrones.solid.dip;

import java.util.List;

/**
 * SEMANA 3: DIP - Servicio de alto nivel que depende de la abstracción IRepositorioDIP,
 * no de una implementación concreta de base de datos.
 */
public class ServicioPublicacionDIP<T> {
    private final IRepositorioDIP<T> repositorio;

    public ServicioPublicacionDIP(IRepositorioDIP<T> repositorio) {
        this.repositorio = repositorio;
    }

    public boolean registrar(T item) {
        return repositorio.guardar(item);
    }

    public List<T> obtenerCatalogo() {
        return repositorio.listarTodos();
    }

    public boolean darDeBaja(int id) {
        return repositorio.eliminar(id);
    }
}

