package patrones.estructurales.bridge;

/**
 * SEMANA 11: Bridge - Implementador concreto para E-Book Digital (-20% respecto al impreso).
 * No consume stock físico de almacén y entrega enlace inmediato (ISP IDigital).
 */
public class FormatoDigitalEbook implements IFormatoBridge {
    @Override
    public String renderizarContenido(String titulo, String autor) {
        return "E-Book EPUB/PDF sin DRM para lector digital: \"" + titulo + "\" por " + autor + " (Descarga instantánea)";
    }

    @Override
    public double costoAdicionalFormato() {
        return 0.00;
    }

    @Override
    public double ajustarPrecio(double precioBase) {
        return Math.max(0.0, precioBase * 0.80); // 20% OFF por formato digital sin costos de imprenta
    }

    @Override
    public boolean consumeStockFisico() {
        return false;
    }

    @Override
    public String getNombreFormato() {
        return "E-Book Digital (-20% OFF)";
    }
}
