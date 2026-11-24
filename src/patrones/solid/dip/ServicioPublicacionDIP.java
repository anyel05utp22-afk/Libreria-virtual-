package patrones.solid.dip;

import java.util.List;

/**
 * SEMANA 3: DIP - Servicio de alto nivel que depende exclusivamente de la abstracción IRepositorioDIP.
 * Permite intercambiar MySQL por almacenamiento en memoria (o pruebas unitarias) sin modificar lógica de negocio.
 */
public class ServicioPublicacionDIP<T> {
    private final IRepositorioDIP<T> repositorio;

    public ServicioPublicacionDIP(IRepositorioDIP<T> repositorio) {
        this.repositorio = repositorio;
    }

    public boolean registrar(T item) {
        return repositorio.guardar(item);
    }

    public int registrarRetornandoId(T item) {
        return repositorio.guardarRetornandoId(item);
    }

    public boolean actualizar(T item) {
        return repositorio.actualizar(item);
    }

    public T buscarPorId(int id) {
        return repositorio.buscarPorId(id);
    }

    public List<T> obtenerCatalogo() {
        return repositorio.listarTodos();
    }

    public boolean darDeBaja(int id) {
        return repositorio.eliminar(id);
    }

    public String getMotorPersistencia() {
        return repositorio.getNombreMotor();
    }
}
