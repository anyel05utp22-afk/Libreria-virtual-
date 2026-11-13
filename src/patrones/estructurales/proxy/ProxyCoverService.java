package patrones.estructurales.proxy;

import java.util.HashMap;
import java.util.Map;

public class ProxyCoverService implements ICoverService {
    private RealCoverService realService;
    private final Map<String, String> cache = new HashMap<>();

    @Override
    public String cargarPortada(String tituloLibro) {
        if (cache.containsKey(tituloLibro)) {
            System.out.println("[Proxy Cache Hit] Portada obtenida de memoria rápida: " + tituloLibro);
            return cache.get(tituloLibro);
        }
        if (realService == null) {
            realService = new RealCoverService();
        }
        System.out.println("[Proxy Cache Miss] Cargando portada mediante RealCoverService...");
        String resultado = realService.cargarPortada(tituloLibro);
        cache.put(tituloLibro, resultado);
        return resultado;
    }
}

