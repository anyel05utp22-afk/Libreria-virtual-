package patrones.solid.dip;

import java.util.List;

/**
 * SEMANA 3: Principio de Inversión de Dependencias (DIP)
 * Interfaz abstracta que define las operaciones del repositorio.
 */
public interface IRepositorioDIP<T> {
    boolean guardar(T entidad);
    T buscarPorId(int id);
    List<T> listarTodos();
    boolean eliminar(int id);
}

