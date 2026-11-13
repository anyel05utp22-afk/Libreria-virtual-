package patrones.estructurales.decorator;

public class PublicacionBase implements IPublicacionComponent {
    private String titulo;
    private double precio;

    public PublicacionBase(String titulo, double precio) {
        this.titulo = titulo;
        this.precio = precio;
    }

    @Override
    public String getDescripcion() {
        return titulo;
    }

    @Override
    public double getPrecioFinal() {
        return precio;
    }
}

