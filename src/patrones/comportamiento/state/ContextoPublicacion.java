package patrones.comportamiento.state;

/**
 * SEMANA 12: Patrón de Comportamiento State (Contexto)
 * Administra el ciclo de vida real de disponibilidad de cada publicación:
 * EstadoDisponible <-> EstadoReservado <-> EstadoAgotado.
 */
public class ContextoPublicacion {
    private String titulo;
    private IEstadoPublicacion estado;
    private int stock;
    private String reservadoPor = "";

    public ContextoPublicacion(String titulo, int stock) {
        this.titulo = titulo;
        this.stock = Math.max(0, stock);
        this.estado = (this.stock > 0) ? new EstadoDisponible() : new EstadoAgotado();
    }

    public void setEstado(IEstadoPublicacion estado) {
        this.estado = estado;
    }

    public IEstadoPublicacion getEstado() {
        return estado;
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = Math.max(0, stock); }
    public String getReservadoPor() { return reservadoPor; }
    public void setReservadoPor(String reservadoPor) { this.reservadoPor = reservadoPor == null ? "" : reservadoPor; }

    public void prestar() {
        estado.prestar(this);
    }

    public void devolver() {
        estado.devolver(this);
    }

    public void reservar(String cliente) {
        this.reservadoPor = (cliente == null || cliente.isBlank()) ? "Lector Web" : cliente.trim();
        this.estado = new EstadoReservado();
        System.out.println("[State] '" + titulo + "' pasó a estado RESERVADO por " + this.reservadoPor);
    }

    public void liberarReserva() {
        this.reservadoPor = "";
        if (this.stock > 0) {
            this.estado = new EstadoDisponible();
        } else {
            this.estado = new EstadoAgotado();
        }
        System.out.println("[State] Reserva liberada para '" + titulo + "' -> " + estado.getNombreEstado());
    }

    public void sincronizarStock(int nuevoStock) {
        this.stock = Math.max(0, nuevoStock);
        if (this.estado instanceof EstadoReservado) {
            // Mantiene reserva activa hasta que se libere explícitamente
            return;
        }
        if (this.stock > 0) {
            this.estado = new EstadoDisponible();
        } else {
            this.estado = new EstadoAgotado();
        }
    }
}
