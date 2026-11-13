package patrones.solid.isp;

/**
 * SEMANA 3: Principio de Segregación de Interfaces (ISP)
 * No forzar a clases a implementar métodos que no necesitan.
 */
public interface IPrestable {
    boolean prestar(String dniCliente);
    void devolver();
    boolean estaPrestado();
}

