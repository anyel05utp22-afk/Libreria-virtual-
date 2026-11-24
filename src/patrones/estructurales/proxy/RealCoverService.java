package patrones.estructurales.proxy;

/**
 * SEMANA 11: Proxy - Servicio Real (RealCoverService)
 * Simula la generación / lectura costosa de metadatos visuales y portada de un libro.
 */
public class RealCoverService implements ICoverService {
    private static final String[][] PALETAS = {
        {"#be123c", "#9f1239"}, {"#047857", "#065f46"}, {"#1e293b", "#0f172a"},
        {"#4338ca", "#3730a3"}, {"#0f766e", "#115e59"}, {"#0369a1", "#075985"},
        {"#9333ea", "#7e22ce"}, {"#d97706", "#b45309"}, {"#b91c1c", "#991b1b"}
    };

    @Override
    public String cargarPortada(String tituloLibro) {
        String safe = (tituloLibro == null || tituloLibro.isBlank()) ? "Libro" : tituloLibro.trim();
        try {
            Thread.sleep(35); // Simula latencia de disco/CDN en cache miss
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        int idx = Math.abs(safe.hashCode()) % PALETAS.length;
        String[] colores = PALETAS[idx];
        String slug = safe.toLowerCase().replaceAll("[^a-z0-9]+", "_");
        return String.format("{\"archivo\":\"cover_%s.png\",\"colorInicio\":\"%s\",\"colorFin\":\"%s\",\"iniciales\":\"%s\"}",
            slug, colores[0], colores[1], extraerIniciales(safe));
    }

    private String extraerIniciales(String titulo) {
        String[] palabras = titulo.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : palabras) {
            if (!p.isEmpty() && sb.length() < 2) {
                sb.append(Character.toUpperCase(p.charAt(0)));
            }
        }
        return sb.length() > 0 ? sb.toString() : "EL";
    }
}
