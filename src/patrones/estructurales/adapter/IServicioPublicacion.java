package patrones.estructurales.adapter;

import patrones.solid.srp.LibroSRP;

public interface IServicioPublicacion {
    boolean insertar(LibroSRP libro);
    boolean actualizar(LibroSRP libro);
    boolean suprimir(int id);
}

