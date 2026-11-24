package patrones.estructurales.bridge;

/**
 * SEMANA 11: Bridge - Implementador concreto para edición impresa física.
 */
public class FormatoFisico implements IFormatoBridge {
    @Override
    public String renderizarContenido(String titulo, String autor) {
        return "Edición Impresa en Papel Couché: \"" + titulo + "\" por " + autor + " (Despacho físico / Recojo en tienda)";
    }

    @Override
    public double costoAdicionalFormato() {
        return 0.00; // Tarifa base impresa sin recargo adicional
    }

    @Override
    public boolean consumeStockFisico() {
        return true;
    }

    @Override
    public String getNombreFormato() {
        return "Impreso Físico";
    }
}
