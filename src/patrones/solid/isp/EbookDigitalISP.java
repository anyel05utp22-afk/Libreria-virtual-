package patrones.solid.isp;

/**
 * SEMANA 3: ISP - EbookDigitalISP implementa únicamente IVendible e IDigital.
 * No se ve forzado a implementar IPrestable (préstamo físico con devolución en mostrador).
 */
public class EbookDigitalISP implements IVendible, IDigital {
    private final int id;
    private final String titulo;
    private final double precio;
    private final String urlDescarga;
    private final long tamanoBytes;

    public EbookDigitalISP(int id, String titulo, double precio) {
        this.id = id;
        this.titulo = titulo;
        this.precio = Math.max(0.0, precio * 0.80); // 20% más económico en digital
        String slug = titulo.toLowerCase().replaceAll("[^a-z0-9]+", "-");
        this.urlDescarga = "/api/descarga-digital?id=" + id + "&archivo=" + slug + ".epub";
        this.tamanoBytes = 1_850_000L + (Math.abs(titulo.hashCode()) % 3_500_000L);
    }

    @Override
    public String getUrlDescarga() {
        return urlDescarga;
    }

    @Override
    public long getTamanoBytes() {
        return tamanoBytes;
    }

    @Override
    public void abrirLectorDigital() {
        System.out.println("[ISP - IDigital] Abriendo lector EPUB para: " + titulo + " (" + urlDescarga + ")");
    }

    @Override
    public double getPrecioVenta() {
        return precio;
    }

    @Override
    public boolean reducirStock(int cantidad) {
        // Las licencias digitales no agotan stock físico de almacén
        return cantidad > 0;
    }

    @Override
    public int getStockDisponible() {
        return 999; // Disponibilidad digital ilimitada
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getTamanoLegible() {
        return String.format(java.util.Locale.US, "%.2f MB", tamanoBytes / (1024.0 * 1024.0));
    }
}
