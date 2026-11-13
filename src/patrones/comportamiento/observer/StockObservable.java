package patrones.comportamiento.observer;

import java.util.ArrayList;
import java.util.List;

public class StockObservable {
    private final List<IObserverCliente> observadores = new ArrayList<>();

    public void suscribir(IObserverCliente obs) {
        if (!observadores.contains(obs)) {
            observadores.add(obs);
            System.out.println("[Observer] Cliente suscrito: " + obs.getNombreCliente());
        }
    }

    public void desuscribir(IObserverCliente obs) {
        observadores.remove(obs);
    }

    public void notificarTodos(String tituloPublicacion, int nuevoStock) {
        String mensaje = String.format("¡Atención! '%s' ha renovado stock disponible (%d unidades).", tituloPublicacion, nuevoStock);
        for (IObserverCliente obs : observadores) {
            obs.notificarNovedad(mensaje);
        }
    }
}

