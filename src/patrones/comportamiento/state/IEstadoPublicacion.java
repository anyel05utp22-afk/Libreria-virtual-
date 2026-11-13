package patrones.comportamiento.state;

/**
 * SEMANA 12: Patrón de Comportamiento State
 * Permite que un objeto altere su comportamiento cuando su estado interno cambia.
 * Modela los estados de disponibilidad visualizados en la interfaz ("Disponible", "Reservar", "Agotado").
 */
public interface IEstadoPublicacion {
    String getNombreEstado();
    String getColorHex();
    boolean permitirReserva();
    boolean permitirPrestamo();
    void prestar(ContextoPublicacion contexto);
    void devolver(ContextoPublicacion contexto);
}

