package patrones.estructurales.proxy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SEMANA 11: Patrón Estructural Proxy (Proxy de Caché Virtual)
 * Intercepta las peticiones de portadas y metadatos visuales hacia RealCoverService,
 * manteniendo una caché concurrente en memoria y métricas en vivo de hits/misses.
 */
public class ProxyCoverService implements ICoverService {
    private RealCoverService realService;
    private final Map<String, String> cache = new ConcurrentHashMap<>();
    private final AtomicInteger cacheHits = new AtomicInteger(0);
    private final AtomicInteger cacheMisses = new AtomicInteger(0);

    @Override
    public String cargarPortada(String tituloLibro) {
        String clave = (tituloLibro == null ? "Libro" : tituloLibro.trim());
        if (cache.containsKey(clave)) {
            cacheHits.incrementAndGet();
            return cache.get(clave);
        }
        synchronized (this) {
            if (realService == null) {
                realService = new RealCoverService();
            }
        }
        cacheMisses.incrementAndGet();
        String resultado = realService.cargarPortada(clave);
        cache.put(clave, resultado);
        return resultado;
    }

    public boolean estaEnCache(String tituloLibro) {
        return tituloLibro != null && cache.containsKey(tituloLibro.trim());
    }

    public int getCacheHits() { return cacheHits.get(); }
    public int getCacheMisses() { return cacheMisses.get(); }
    public int getTamanoCache() { return cache.size(); }

    public double getHitRatePorcentaje() {
        int total = cacheHits.get() + cacheMisses.get();
        return total == 0 ? 0.0 : (cacheHits.get() * 100.0) / total;
    }
}
