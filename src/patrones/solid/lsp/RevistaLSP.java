package patrones.solid.lsp;

public class RevistaLSP extends PublicacionLSP {
    private String marca;
    private int edicion;
    private String fechaPublicacion;

    public RevistaLSP(int id, String titulo, String marca, int edicion, String fechaPublicacion, double precio, int stock) {
        super(id, titulo, precio, stock);
        this.marca = marca;
        this.edicion = edicion;
        this.fechaPublicacion = fechaPublicacion;
    }

    @Override
    public String getTipo() {
        return "Revista";
    }

    @Override
    public String getFichaTecnica() {
        return String.format("Revista: %s | Marca: %s | Edición: #%d | Fecha: %s",
            titulo, marca, edicion, fechaPublicacion);
    }

    public String getMarca() { return marca; }
    public int getEdicion() { return edicion; }
    public String getFechaPublicacion() { return fechaPublicacion; }
}

