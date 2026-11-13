package patrones.solid.ocp;

/**
 * SEMANA 2: Principio Abierto / Cerrado (OCP)
 * Abierto a extensión, cerrado a modificación mediante polimorfismo.
 */
public interface IDescuentoOCP {
    double calcularDescuento(double precioBase);
    String getDescripcion();
}

