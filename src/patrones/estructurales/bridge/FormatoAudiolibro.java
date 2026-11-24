package patrones.estructurales.bridge;

/**
 * SEMANA 11: Bridge - Implementador concreto para Audiolibro HD (+S/ 6.00 narración profesional).
 */
public class FormatoAudiolibro implements IFormatoBridge {
    @Override
    public String renderizarContenido(String titulo, String autor) {
        return "Audiolibro HD narrado en estudio (MP3/M4B): \"" + titulo + "\" por " + autor + " (Acceso streaming + offline)";
    }

    @Override
    public double costoAdicionalFormato() {
        return 6.00;
    }

    @Override
    public boolean consumeStockFisico() {
        return false;
    }

    @Override
    public String getNombreFormato() {
        return "Audiolibro HD (+S/ 6.00)";
    }
}
