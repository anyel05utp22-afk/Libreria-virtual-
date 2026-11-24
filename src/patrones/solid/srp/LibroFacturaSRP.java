package patrones.solid.srp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SEMANA 1: SRP - Responsabilidad exclusiva de formatear y emitir comprobantes de venta.
 * Separada de la entidad LibroSRP, la validación y la persistencia.
 */
public class LibroFacturaSRP {

    private static final AtomicInteger SECUENCIA = new AtomicInteger(1000);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static String generarNumeroBoleta() {
        return "B001-" + SECUENCIA.incrementAndGet();
    }

    public static String obtenerFechaActual() {
        return LocalDateTime.now().format(FMT);
    }

    public static String generarComprobante(LibroSRP libro, int cantidad) {
        double subtotal = libro.getPrecio() * cantidad;
        double igv = subtotal * 0.18;
        double total = subtotal + igv;
        return String.format(Locale.US,
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

    public static String generarComprobanteDetallado(
            String cliente,
            List<String> detalleLineas,
            String formatoEntrega,
            String descripcionDescuento,
            double montoDescuento,
            String descripcionDecoradores,
            double montoExtras,
            double subtotalNeto) {

        int numero = SECUENCIA.incrementAndGet();
        String fecha = LocalDateTime.now().format(FMT);
        double baseImponible = Math.max(0.0, subtotalNeto);
        double igv = baseImponible * 0.18;
        double totalConIgv = baseImponible + igv;

        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("        LIBRERÍA EL SABER S.A.C.\n");
        sb.append("      BOLETA ELECTRÓNICA #B001-").append(numero).append("\n");
        sb.append("==========================================\n");
        sb.append("Fecha:   ").append(fecha).append("\n");
        sb.append("Cliente: ").append(cliente == null || cliente.isBlank() ? "Cliente Web" : cliente).append("\n");
        sb.append("Entrega: ").append(formatoEntrega).append("\n");
        sb.append("------------------------------------------\n");
        for (String linea : detalleLineas) {
            sb.append(" • ").append(linea).append("\n");
        }
        sb.append("------------------------------------------\n");
        sb.append(String.format(Locale.US, "Promoción (%s): -S/ %.2f\n", descripcionDescuento, montoDescuento));
        if (montoExtras > 0) {
            sb.append(String.format(Locale.US, "Extras (%s): +S/ %.2f\n", descripcionDecoradores, montoExtras));
        }
        sb.append(String.format(Locale.US, "Op. Gravada (Subtotal):  S/ %.2f\n", baseImponible));
        sb.append(String.format(Locale.US, "IGV (18%%):               S/ %.2f\n", igv));
        sb.append("==========================================\n");
        sb.append(String.format(Locale.US, "TOTAL A PAGAR:           S/ %.2f\n", totalConIgv));
        sb.append("==========================================\n");
        sb.append("Gracias por apoyar la lectura en El Saber.");
        return sb.toString();
    }
}
