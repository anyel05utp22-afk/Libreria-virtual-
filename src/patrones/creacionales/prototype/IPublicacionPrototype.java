package patrones.creacionales.prototype;

/**
 * SEMANA 6: Patrón Creacional Prototype
 * Permite clonar objetos existentes sin hacer que el código dependa de sus clases.
 */
public interface IPublicacionPrototype extends Cloneable {
    IPublicacionPrototype clonar();
    String getInfo();
}

