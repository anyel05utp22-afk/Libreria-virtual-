package patrones.estructurales.bridge;

/**
 * SEMANA 11: Bridge (Abstracción)
 */
public abstract class PublicacionBridge {
    protected IFormatoBridge formato;
    protected String titulo;
    protected String autor;
    protected double precioBase;

    public PublicacionBridge(IFormatoBridge formato, String titulo, String autor, double precioBase) {
        this.formato = formato;
        this.titulo = titulo;
        this.autor = autor;
        this.precioBase = precioBase;
    }

    public abstract String getDetalleEntrega();

    public double getPrecioFinal() {
        return precioBase + formato.costoAdicionalFormato();
    }
}

