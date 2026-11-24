package patrones.estructurales.bridge;

/**
 * SEMANA 11: Patrón Estructural Bridge (Abstracción)
 * Mantiene una referencia al implementador IFormatoBridge y delega en él
 * la renderización del formato y el ajuste de precio.
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
        return formato.ajustarPrecio(precioBase);
    }

    public boolean requiereStockFisico() {
        return formato.consumeStockFisico();
    }

    public String getNombreFormato() {
        return formato.getNombreFormato();
    }

    public static IFormatoBridge resolverFormato(String clave) {
        if (clave == null) return new FormatoFisico();
        switch (clave.trim().toLowerCase()) {
            case "digital":
            case "ebook":
                return new FormatoDigitalEbook();
            case "audiolibro":
            case "audio":
                return new FormatoAudiolibro();
            default:
                return new FormatoFisico();
        }
    }
}
