package patrones.solid.ocp;

/**
 * SEMANA 2: OCP - Extensión para clientes frecuentes / club de lectores (10% OFF).
 * Agregada sin modificar CalculadoraPrecioOCP.
 */
public class DescuentoClienteFrecuente implements IDescuentoOCP {
    @Override
    public double calcularDescuento(double precioBase) {
        return precioBase * 0.10;
    }

    @Override
    public String getDescripcion() {
        return "Club Lector Frecuente (10%)";
    }
}
