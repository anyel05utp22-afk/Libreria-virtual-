package patrones.estructurales.decorator;

/**
 * SEMANA 9: Patrón Estructural Decorator
 * Añade dinámicamente el servicio de Envío Express 24h (+S/ 8.00) a cualquier publicación.
 */
public class EnvioExpressDecorator extends PublicacionDecorator {
    public static final double COSTO_ENVIO_EXPRESS = 8.00;

    public EnvioExpressDecorator(IPublicacionComponent componente) {
        super(componente);
    }

    @Override
    public String getDescripcion() {
        return componente.getDescripcion() + " + [Envío Express 24h]";
    }

    @Override
    public double getPrecioFinal() {
        return componente.getPrecioFinal() + COSTO_ENVIO_EXPRESS;
    }
}
