package patrones.comportamiento.state;

public class ContextoPublicacion {
    private String titulo;
    private IEstadoPublicacion estado;
    private int stock;

    public ContextoPublicacion(String titulo, int stock) {
        this.titulo = titulo;
        this.stock = stock;
        if (stock > 0) {
            this.estado = new EstadoDisponible();
        } else {
            this.estado = new EstadoAgotado();
        }
    }

    public void setEstado(IEstadoPublicacion estado) {
        this.estado = estado;
    }

    public IEstadoPublicacion getEstado() {
        return estado;
    }

    public String getTitulo() { return titulo; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public void prestar() {
        estado.prestar(this);
    }

    public void devolver() {
        estado.devolver(this);
    }
}

