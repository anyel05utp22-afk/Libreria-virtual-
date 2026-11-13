package patrones.comportamiento.observer;

/**
 * SEMANA 12: Patrón de Comportamiento Observer
 * Define una dependencia uno-a-muchos entre objetos, para que cuando un objeto cambie de estado,
 * todos sus dependientes sean notificados automáticamente.
 */
public interface IObserverCliente {
    void notificarNovedad(String mensaje);
    String getNombreCliente();
}

