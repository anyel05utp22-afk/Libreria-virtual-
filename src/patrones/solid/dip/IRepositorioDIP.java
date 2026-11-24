package patrones.solid.dip;

import java.util.List;

/**
 * SEMANA 3: Principio de Inversión de Dependencias (DIP)
 * Interfaz abstracta que define el contrato de persistencia.
 * Los módulos de alto nivel (Facade, Controller, Servidor) dependen de esta abstracción,
 * nunca directamente de JDBC o almacenamiento en memoria.
 */
public interface IRepositorioDIP<T> {
    boolean guardar(T entidad);
    T buscarPorId(int id);
    List<T> listarTodos();
    boolean eliminar(int id);

    default boolean actualizar(T entidad) {
        return false;
    }

    default int guardarRetornandoId(T entidad) {
        return guardar(entidad) ? 1 : -1;
    }

    default String getNombreMotor() {
        return getClass().getSimpleName();
    }
}
