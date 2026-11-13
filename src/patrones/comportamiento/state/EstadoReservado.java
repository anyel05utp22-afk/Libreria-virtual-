package patrones.comportamiento.state;

public class EstadoReservado implements IEstadoPublicacion {
    @Override
    public String getNombreEstado() {
        return "Reservar";
    }

    @Override
    public String getColorHex() {
        return "#E65100"; // Naranja / Ambar
    }

    @Override
    public boolean permitirReserva() {
        return true;
    }

    @Override
    public boolean permitirPrestamo() {
        return false;
    }

    @Override
    public void prestar(ContextoPublicacion contexto) {
        System.out.println("[State] No se puede prestar de inmediato; se encuentra en cola de reserva.");
    }

    @Override
    public void devolver(ContextoPublicacion contexto) {
        contexto.setEstado(new EstadoDisponible());
        System.out.println("[State] Reserva liberada; ahora DISPONIBLE.");
    }
}
