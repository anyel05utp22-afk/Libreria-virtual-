package patrones.grasp.creador;

import patrones.solid.srp.LibroSRP;

/**
 * SEMANA 16: Patrón GRASP - Creador (Creator)
 * Determina quién debe ser responsable de crear nuevas instancias de una clase.
 * Una clase B debe crear instancias de A si B contiene, agrega o utiliza cercanamente a A.
 */
public class VentaItemCreator {
    public static class LineaVenta {
        private final LibroSRP libro;
        private final int cantidad;
        private final double subtotal;

        public LineaVenta(LibroSRP libro, int cantidad) {
            this.libro = libro;
            this.cantidad = cantidad;
            this.subtotal = libro.getPrecio() * cantidad;
        }

        public LibroSRP getLibro() { return libro; }
        public int getCantidad() { return cantidad; }
        public double getSubtotal() { return subtotal; }
    }

    public static LineaVenta crearLinea(LibroSRP libro, int cantidad) {
        return new LineaVenta(libro, cantidad);
    }
}

