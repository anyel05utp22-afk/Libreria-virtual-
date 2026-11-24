package patrones.estructurales.decorator;

/**
 * SEMANA 9: Patrón Estructural Decorator
 * Añade dinámicamente cobertura de garantía de reposición por 30 días (+S/ 3.50)
 * envolviendo cualquier IPublicacionComponent sin modificar la entidad base.
 */
public class GarantiaExtendidaDecorator extends PublicacionDecorator {
    public static final double COSTO_GARANTIA = 3.50;

    public GarantiaExtendidaDecorator(IPublicacionComponent componente) {
        super(componente);
    }

    @Override
    public String getDescripcion() {
        return componente.getDescripcion() + " + [Garantía de Reposición 30 días]";
    }

    @Override
    public double getPrecioFinal() {
        return componente.getPrecioFinal() + COSTO_GARANTIA;
    }
}
