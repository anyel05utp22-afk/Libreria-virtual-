package patrones.comportamiento.state;

public class EstadoAgotado implements IEstadoPublicacion {
    @Override
    public String getNombreEstado() {
        return "Agotado";
    }

    @Override
    public String getColorHex() {
        return "#C62828"; // Rojo oscuro
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
        System.out.println("[State] Operación denegada: Producto agotado.");
    }

    @Override
    public void devolver(ContextoPublicacion contexto) {
        contexto.setStock(contexto.getStock() + 1);
        contexto.setEstado(new EstadoDisponible());
        System.out.println("[State] Reingreso de stock: Ahora DISPONIBLE.");
    }
}

