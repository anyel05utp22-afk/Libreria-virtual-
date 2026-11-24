package patrones.solid.ocp;

/**
 * SEMANA 2: OCP - Extensión para compras por volumen o carrito mayor a S/ 100 (20% OFF).
 * Agregada sin modificar CalculadoraPrecioOCP.
 */
public class DescuentoVolumen implements IDescuentoOCP {
    @Override
    public double calcularDescuento(double precioBase) {
        return precioBase >= 100.0 ? precioBase * 0.20 : precioBase * 0.12;
    }

    @Override
    public String getDescripcion() {
        return "Descuento Por Volumen (20% >= S/100)";
    }
}
