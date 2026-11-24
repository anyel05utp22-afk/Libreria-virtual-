package patrones.estructurales.bridge;

/**
 * SEMANA 11: Bridge - Abstracción refinada para Revistas y Cuentos.
 */
public class RevistaBridge extends PublicacionBridge {
    private final int edicion;

    public RevistaBridge(IFormatoBridge formato, String titulo, String marca, int edicion, double precioBase) {
        super(formato, titulo, marca, precioBase);
        this.edicion = edicion;
    }

    @Override
    public String getDetalleEntrega() {
        return "[Revista Ed. #" + edicion + "] " + formato.renderizarContenido(titulo, autor);
    }
}
