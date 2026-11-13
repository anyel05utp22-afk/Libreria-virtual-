package patrones.solid.ocp;

public class DescuentoEstudiante implements IDescuentoOCP {
    @Override
    public double calcularDescuento(double precioBase) {
        return precioBase * 0.15; // 15% de descuento
    }

    @Override
    public String getDescripcion() {
        return "Descuento Estudiante (15%)";
    }
}

