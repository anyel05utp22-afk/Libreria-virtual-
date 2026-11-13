package patrones.solid.isp;

/**
 * SEMANA 3: ISP - LibroFisico solo implementa IPrestable y IVendible.
 * No se ve forzado a implementar IDigital.
 */
public class LibroFisicoISP implements IPrestable, IVendible {
    private String titulo;
    private double precio;
    private int stock;
    private boolean prestado;

    public LibroFisicoISP(String titulo, double precio, int stock) {
        this.titulo = titulo;
        this.precio = precio;
        this.stock = stock;
        this.prestado = false;
    }

    @Override
    public boolean prestar(String dniCliente) {
        if (!prestado && stock > 0) {
            prestado = true;
            stock--;
            return true;
        }
        return false;
    }

    @Override
    public void devolver() {
        if (prestado) {
            prestado = false;
            stock++;
        }
    }

    @Override
    public boolean estaPrestado() {
        return prestado;
    }

    @Override
    public double getPrecioVenta() {
        return precio;
    }

    @Override
    public boolean reducirStock(int cantidad) {
        if (stock >= cantidad) {
            stock -= cantidad;
            return true;
        }
        return false;
    }

    @Override
    public int getStockDisponible() {
        return stock;
    }

    public String getTitulo() { return titulo; }
}

