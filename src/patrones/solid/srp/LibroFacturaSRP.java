package patrones.solid.srp;

/**
 * SEMANA 1: SRP - Responsabilidad exclusiva de formatear o generar comprobante de venta
 */
public class LibroFacturaSRP {
    public static String generarComprobante(LibroSRP libro, int cantidad) {
        double subtotal = libro.getPrecio() * cantidad;
        double igv = subtotal * 0.18;
        double total = subtotal + igv;
        return String.format(
            "==== COMPROBANTE DE VENTA ====\n" +
            "Item: %s\n" +
            "Autor: %s\n" +
            "Cantidad: %d\n" +
            "Precio unitario: S/ %.2f\n" +
            "Subtotal: S/ %.2f\n" +
            "IGV (18%%): S/ %.2f\n" +
            "TOTAL: S/ %.2f\n" +
            "==============================",
            libro.getTitulo(), libro.getAutor(), cantidad, libro.getPrecio(), subtotal, igv, total
        );
    }
}

