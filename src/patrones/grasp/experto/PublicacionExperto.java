package patrones.grasp.experto;

/**
 * SEMANA 17: Patrón GRASP - Experto en Información (Information Expert)
 * Asigna la responsabilidad al que posee la información necesaria para cumplirla.
 * PublicacionExperto calcula el importe neto, impuestos y umbrales de reabastecimiento
 * porque tiene acceso directo a los campos precio y stock.
 */
public class PublicacionExperto {
    private String titulo;
    private double precioBase;
    private int stockActual;
    private int stockMinimoAlerta;

    public PublicacionExperto(String titulo, double precioBase, int stockActual, int stockMinimoAlerta) {
        this.titulo = titulo;
        this.precioBase = precioBase;
        this.stockActual = stockActual;
        this.stockMinimoAlerta = stockMinimoAlerta;
    }

    public double calcularPrecioConIGV() {
        return precioBase * 1.18;
    }

    public double calcularValorTotalInventario() {
        return precioBase * stockActual;
    }

    public boolean requiereReabastecimiento() {
        return stockActual <= stockMinimoAlerta;
    }

    public String getTitulo() { return titulo; }
    public double getPrecioBase() { return precioBase; }
    public int getStockActual() { return stockActual; }
}

