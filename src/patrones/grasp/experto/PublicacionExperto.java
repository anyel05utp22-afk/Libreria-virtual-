package patrones.grasp.experto;

/**
 * SEMANA 17: Patrón GRASP - Experto en Información (Information Expert)
 * Asigna los cálculos de impuesto (IGV), valorización de stock y alertas de reposición
 * directamente a la clase que posee los datos de precio y existencias.
 */
public class PublicacionExperto {
    private final String titulo;
    private final double precioBase;
    private final int stockActual;
    private final int stockMinimoAlerta;

    public PublicacionExperto(String titulo, double precioBase, int stockActual, int stockMinimoAlerta) {
        this.titulo = titulo;
        this.precioBase = Math.max(0.0, precioBase);
        this.stockActual = Math.max(0, stockActual);
        this.stockMinimoAlerta = Math.max(0, stockMinimoAlerta);
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

    public String getNivelAlerta() {
        if (stockActual == 0) return "AGOTADO";
        if (stockActual <= stockMinimoAlerta) return "STOCK_BAJO";
        return "OPTIMO";
    }

    public String getTitulo() { return titulo; }
    public double getPrecioBase() { return precioBase; }
    public int getStockActual() { return stockActual; }
    public int getStockMinimoAlerta() { return stockMinimoAlerta; }
}
