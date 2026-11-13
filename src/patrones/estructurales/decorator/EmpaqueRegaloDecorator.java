package patrones.estructurales.decorator;

public class EmpaqueRegaloDecorator extends PublicacionDecorator {
    public EmpaqueRegaloDecorator(IPublicacionComponent componente) {
        super(componente);
    }

    @Override
    public String getDescripcion() {
        return componente.getDescripcion() + " + [Empaque de Regalo con Moño]";
    }

    @Override
    public double getPrecioFinal() {
        return componente.getPrecioFinal() + 5.00; // S/ 5 por empaque de regalo
    }
}

