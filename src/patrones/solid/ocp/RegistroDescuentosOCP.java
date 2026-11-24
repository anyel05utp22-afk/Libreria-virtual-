package patrones.solid.ocp;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SEMANA 2: OCP - Registro extensible de estrategias de descuento.
 * Permite añadir nuevas promociones en tiempo de ejecución sin alterar la calculadora central.
 */
public class RegistroDescuentosOCP {
    private static final Map<String, IDescuentoOCP> ESTRATEGIAS = new LinkedHashMap<>();

    static {
        registrar("ninguno", new SinDescuento());
        registrar("frecuente", new DescuentoClienteFrecuente());
        registrar("estudiante", new DescuentoEstudiante());
        registrar("volumen", new DescuentoVolumen());
        registrar("temporada", new DescuentoTemporada());
    }

    public static void registrar(String clave, IDescuentoOCP descuento) {
        if (clave != null && descuento != null) {
            ESTRATEGIAS.put(clave.toLowerCase(), descuento);
        }
    }

    public static IDescuentoOCP obtener(String clave) {
        if (clave == null) return ESTRATEGIAS.get("ninguno");
        return ESTRATEGIAS.getOrDefault(clave.toLowerCase(), ESTRATEGIAS.get("ninguno"));
    }

    public static Map<String, IDescuentoOCP> listarTodas() {
        return Collections.unmodifiableMap(ESTRATEGIAS);
    }
}
