package patrones.comportamiento.state;

public class EstadoDisponible implements IEstadoPublicacion {
    @Override
    public String getNombreEstado() {
        return "Disponible";
    }

    @Override
    public String getColorHex() {
        return "#2E7D32"; // Verde material
    }

    @Override
    public boolean permitirReserva() {
        return false; // Está listo para compra/préstamo directo
    }

    @Override
    public boolean permitirPrestamo() {
        return true;
    }

    @Override
    public void prestar(ContextoPublicacion contexto) {
        contexto.setStock(contexto.getStock() - 1);
        if (contexto.getStock() <= 0) {
            contexto.setEstado(new EstadoAgotado());
            System.out.println("[State] " + contexto.getTitulo() + " ahora está AGOTADO.");
        } else {
            System.out.println("[State] " + contexto.getTitulo() + " prestado. Stock restante: " + contexto.getStock());
        }
    }

    @Override
    public void devolver(ContextoPublicacion contexto) {
        contexto.setStock(contexto.getStock() + 1);
    }
}

