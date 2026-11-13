package patrones.estructurales.proxy;

public class RealCoverService implements ICoverService {
    @Override
    public String cargarPortada(String tituloLibro) {
        // Simula la lectura o descarga de disco/red
        try {
            Thread.sleep(80); // Simulación de I/O pesado
        } catch (InterruptedException ignored) {}
        return "portada_renderizada_" + tituloLibro.replaceAll("\\s+", "_").toLowerCase() + ".png";
    }
}

