package patrones.comportamiento.observer;

public class ClienteObserver implements IObserverCliente {
    private String nombre;
    private String email;

    public ClienteObserver(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    @Override
    public void notificarNovedad(String mensaje) {
        System.out.println("[Notificación para " + nombre + " (" + email + ")]: " + mensaje);
    }

    @Override
    public String getNombreCliente() {
        return nombre;
    }
}

