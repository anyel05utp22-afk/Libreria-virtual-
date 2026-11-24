package patrones.grasp.creador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import patrones.solid.srp.LibroSRP;

/**
 * SEMANA 16: Patrón GRASP - Creador (Creator)
 * Asigna la responsabilidad de crear objetos LineaVenta al contenedor/orden que los agrega
 * y posee los datos necesarios para inicializarlos.
 */
public class VentaItemCreator {

    public static class LineaVenta {
        private final LibroSRP libro;
        private final int cantidad;
        private final double precioUnitario;
        private final double subtotal;

        public LineaVenta(LibroSRP libro, int cantidad) {
            this(libro, cantidad, libro.getPrecio());
        }

        public LineaVenta(LibroSRP libro, int cantidad, double precioUnitarioAjustado) {
            this.libro = libro;
            this.cantidad = Math.max(1, cantidad);
            this.precioUnitario = Math.max(0.0, precioUnitarioAjustado);
            this.subtotal = this.precioUnitario * this.cantidad;
        }

        public LibroSRP getLibro() { return libro; }
        public int getCantidad() { return cantidad; }
        public double getPrecioUnitario() { return precioUnitario; }
        public double getSubtotal() { return subtotal; }

        public String formatearLinea() {
            return String.format(Locale.US, "%s x%d (S/ %.2f c/u) = S/ %.2f",
                libro.getTitulo(), cantidad, precioUnitario, subtotal);
        }
    }

    /**
     * Contenedor de venta (Carrito) que actúa como Creator de sus LineaVenta.
     */
    public static class OrdenVenta {
        private final List<LineaVenta> lineas = new ArrayList<>();

        public LineaVenta agregarItem(LibroSRP libro, int cantidad, double precioUnitarioAjustado) {
            LineaVenta linea = new LineaVenta(libro, cantidad, precioUnitarioAjustado);
            lineas.add(linea);
            return linea;
        }

        public List<LineaVenta> getLineas() {
            return Collections.unmodifiableList(lineas);
        }

        public double calcularSubtotalBruto() {
            double total = 0.0;
            for (LineaVenta lv : lineas) {
                total += lv.getSubtotal();
            }
            return total;
        }

        public int getTotalUnidades() {
            int total = 0;
            for (LineaVenta lv : lineas) {
                total += lv.getCantidad();
            }
            return total;
        }

        public List<String> getDescripcionesLineas() {
            List<String> res = new ArrayList<>();
            for (LineaVenta lv : lineas) {
                res.add(lv.formatearLinea());
            }
            return res;
        }
    }

    public static LineaVenta crearLinea(LibroSRP libro, int cantidad) {
        return new LineaVenta(libro, cantidad);
    }

    public static OrdenVenta crearOrden() {
        return new OrdenVenta();
    }
}
