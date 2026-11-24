package patrones.solid.ocp;

/**
 * SEMANA 2: OCP - Implementación Null Object para tarifa regular sin descuento.
 */
public class SinDescuento implements IDescuentoOCP {
    @Override
    public double calcularDescuento(double precioBase) {
        return 0.0;
    }

    @Override
    public String getDescripcion() {
        return "Tarifa Regular (0%)";
    }
}
