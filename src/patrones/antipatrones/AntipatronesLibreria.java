package patrones.antipatrones;

/**
 * SEMANA 14: Antipatrones de Desarrollo de Software y Organizacionales
 * Documentación y contraste en código entre malas prácticas (antipatrones)
 * y la solución refactorizada con patrones de diseño.
 */
public class AntipatronesLibreria {

    /**
     * ANTIPATRÓN 1: The God Object / The Blob (Desarrollo)
     * Una sola clase gigante que contiene la UI, la lógica de negocio,
     * las conexiones directas a base de datos, el cálculo de impuestos y los reportes.
     * SOLUCIÓN: Separar en capas con SRP, MVC, Facade y Repositorios.
     */
    public static class AntipatronGodObjectEjemplo {
        public void procesarTodoEnUnaSolaClase() {
            // Anti-patrón: Mezcla UI + JDBC + Validaciones + Facturación + Envío de Email
            System.out.println("[Antipatrón God Object] Una clase de 3000 líneas que hace todo y es frágil.");
        }
    }

    /**
     * ANTIPATRÓN 2: Spaghetti Code / Hardcoded SQL (Desarrollo)
     * Consultas SQL dispersas por botones de interfaz gráfica sin control de excepciones
     * ni transacciones, con alta redundancia.
     * SOLUCIÓN: Data Access Object (DAO) / Repository + Factory + Transacciones.
     */
    public static class AntipatronSpaghettiEjemplo {
        public void queryEnElBoton() {
            System.out.println("[Antipatrón Spaghetti Code] SQL hardcodeado en ActionListener de botones Swing.");
        }
    }

    /**
     * ANTIPATRÓN 3: Lava Flow (Flujo de Lava) (Desarrollo)
     * Código muerto o experimental de versiones preliminares que nadie se atreve a borrar por miedo a romper algo.
     * SOLUCIÓN: Control de versiones (Git), pruebas unitarias automatizadas y refactorización continua.
     */
    public static class AntipatronLavaFlowEjemplo {
        public void metodoAntiguoQueNadieBorra() {
            System.out.println("[Antipatrón Lava Flow] Código obsoleto preservado 'por si acaso'.");
        }
    }

    /**
     * ANTIPATRÓN 4: Golden Hammer (Martillo de Oro) (Desarrollo)
     * Creer que un mismo patrón (por ejemplo Singleton) resuelve absolutamente todos los problemas.
     * SOLUCIÓN: Evaluar cada patrón de diseño según su propósito (GoF Creacional, Estructural, Comportamiento).
     */

    /**
     * ANTIPATRÓN 5: Analysis Paralysis (Parálisis por Análisis) (Organizacional)
     * El equipo pasa semanas debatiendo qué arquitectura perfecta usar sin escribir código ni validar con el usuario.
     * SOLUCIÓN: Metodologías ágiles, entregas iterativas e incrementales (Sprints).
     */

    /**
     * ANTIPATRÓN 6: Bicycle Shedding / Ley de la Trivialidad de Parkinson (Organizacional)
     * Discutir durante horas el color de un botón o la tipografía, mientras se ignoran fallas graves de seguridad o diseño.
     * SOLUCIÓN: Priorización de requerimientos basada en valor arquitectónico y funcional.
     */
}

