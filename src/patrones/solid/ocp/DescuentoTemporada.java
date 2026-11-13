package patrones.solid.ocp;

public class DescuentoTemporada implements IDescuentoOCP {
    @Override
    public double calcularDescuento(double precioBase) {
        return precioBase * 0.25; // 25% descuento ferias de libro
    }

    @Override
    public String getDescripcion() {
        return "Descuento Feria del Libro (25%)";
    }
}

