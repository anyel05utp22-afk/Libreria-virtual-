package patrones.estructurales.decorator;

public abstract class PublicacionDecorator implements IPublicacionComponent {
    protected IPublicacionComponent componente;

    public PublicacionDecorator(IPublicacionComponent componente) {
        this.componente = componente;
    }

    @Override
    public String getDescripcion() {
        return componente.getDescripcion();
    }

    @Override
    public double getPrecioFinal() {
        return componente.getPrecioFinal();
    }
}

