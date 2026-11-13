package patrones.solid.ocp;

/**
 * SEMANA 2: OCP - Esta clase no se modifica cuando se agregan nuevos descuentos.
 * Solo se extiende implementando IDescuentoOCP.
 */
public class CalculadoraPrecioOCP {
    public static double calcularPrecioFinal(double precioBase, IDescuentoOCP descuento) {
        if (descuento == null) return precioBase;
        double rebaja = descuento.calcularDescuento(precioBase);
        return Math.max(0.0, precioBase - rebaja);
    }
}

