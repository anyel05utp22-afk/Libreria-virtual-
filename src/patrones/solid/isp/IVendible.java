package patrones.solid.isp;

public interface IVendible {
    double getPrecioVenta();
    boolean reducirStock(int cantidad);
    int getStockDisponible();
}

