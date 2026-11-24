package patrones.creacionales.prototype;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SEMANA 6: Patrón Prototype - Registro (Catálogo) de Prototipos preconfigurados.
 * Permite crear rápidamente nuevas fichas clonando plantillas editoriales estándar.
 */
public class RegistroPrototipos {
    private static final Map<String, LibroPrototype> PLANTILLAS = new LinkedHashMap<>();

    static {
        PLANTILLAS.put("bestseller_amor", new LibroPrototype(
            "Autor Contemporáneo", "Nueva Novela Romántica", "Planeta",
            "Recomendados de amor", 42.00, 10));
        PLANTILLAS.put("clasico_coleccion", new LibroPrototype(
            "Autor Clásico Universal", "Obra Clásica Anotada", "Alfaguara",
            "Clásicos", 48.00, 8));
        PLANTILLAS.put("tecnologia_ia", new LibroPrototype(
            "Especialista en Sistemas", "Arquitectura y Patrones de Software", "Ra-Ma",
            "Filosofía, Educación, Ciencia y Tecnología", 64.00, 12));
        PLANTILLAS.put("vacaciones_cuentos", new LibroPrototype(
            "Antología Varios Autores", "Relatos de Verano", "Seix Barral",
            "Novelas y cuentos para las vacaciones", 34.00, 15));
    }

    public static LibroPrototype obtenerClon(String clave) {
        LibroPrototype base = PLANTILLAS.get(clave);
        if (base == null) return null;
        return base.clonarConEdicion("Plantilla");
    }

    public static Map<String, LibroPrototype> listarPlantillas() {
        return Collections.unmodifiableMap(PLANTILLAS);
    }
}
