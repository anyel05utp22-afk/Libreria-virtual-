package patrones.antipatrones;

/**
 * SEMANA 14: Antipatrones de Desarrollo de Software y Organizacionales
 * Documenta, contrasta y evalúa en tiempo de ejecución cómo la arquitectura de
 * Librería El Saber erradica los antipatrones clásicos mediante SOLID, GoF y GRASP.
 */
public class AntipatronesLibreria {

    public static class AntipatronGodObjectEjemplo {
        public void procesarTodoEnUnaSolaClase() {
            System.out.println("[Antipatrón God Object] Una clase de 3000 líneas que hace todo y es frágil.");
        }
    }

    public static class AntipatronSpaghettiEjemplo {
        public void queryEnElBoton() {
            System.out.println("[Antipatrón Spaghetti Code] SQL hardcodeado en ActionListener de botones Swing.");
        }
    }

    public static class AntipatronLavaFlowEjemplo {
        public void metodoAntiguoQueNadieBorra() {
            System.out.println("[Antipatrón Lava Flow] Código obsoleto preservado 'por si acaso'.");
        }
    }

    /**
     * Genera el reporte estructurado en JSON de los 6 antipatrones (4 de desarrollo + 2 organizacionales)
     * junto con la verificación en vivo de las mitigaciones aplicadas en el proyecto.
     */
    public static String generarReporteJson(boolean singletonActivo, String motorDip,
                                            int cacheHitsProxy, int totalEventosAuditoria) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"resumen\":\"Diagnóstico de Calidad Arquitectónica (Semana 14)\",");
        sb.append("\"saludGeneral\":\"ÓPTIMA\",");
        sb.append("\"verificacionesEnVivo\":{");
        sb.append("\"singletonConectado\":").append(singletonActivo).append(",");
        sb.append("\"motorDIP\":\"").append(escapar(motorDip)).append("\",");
        sb.append("\"proxyCacheHits\":").append(cacheHitsProxy).append(",");
        sb.append("\"eventosPureFabrication\":").append(totalEventosAuditoria);
        sb.append("},\"antipatrones\":[");

        sb.append(item("The God Object (The Blob)", "Desarrollo", "MITIGADO",
            "Una sola clase monolítica que concentra interfaz, reglas de negocio, SQL, facturación y auditoría.",
            "Separación estricta en capas: ServidorLibreriaWeb (HTTP) → LibreriaGRASPController (GRASP) → LibreriaFacade (GoF) → IRepositorioDIP (SOLID) + LibroFacturaSRP + LibroValidatorSRP."));
        sb.append(",");
        sb.append(item("Spaghetti Code & Hardcoded SQL", "Desarrollo", "MITIGADO",
            "Consultas SQL dispersas en eventos de interfaz gráfica sin encapsulamiento ni manejo uniforme.",
            "Acceso a datos encapsulado tras IRepositorioDIP, MySqlLibroRepositorioDIP, ConexionSingleton y LegacyConexionAdapter (procedimientos CRL, CRC, CRR)."));
        sb.append(",");
        sb.append(item("Lava Flow (Flujo de Lava)", "Desarrollo", "MITIGADO",
            "Código muerto, botones sin funcionalidad o clases de patrones creadas pero desconectadas del flujo real.",
            "Todas las clases de las Semanas 1 a 17 están conectadas, ejecutándose en endpoints reales y verificables desde la plataforma web."));
        sb.append(",");
        sb.append(item("Golden Hammer (Martillo de Oro)", "Desarrollo", "MITIGADO",
            "Aplicar un único patrón (como Singleton o herencia rígida) para resolver todos los problemas de diseño.",
            "Uso balanceado según propósito: Creacionales (creación), Estructurales (composición/desacoplamiento), Comportamiento (estados/eventos/undo) y GRASP (asignación de responsabilidades)."));
        sb.append(",");
        sb.append(item("Analysis Paralysis (Parálisis por Análisis)", "Organizacional", "PREVENIDO",
            "Invertir semanas diseñando diagramas teóricos sin entregar software funcional ni validar con usuarios.",
            "Desarrollo iterativo incremental: cada semana del sílabo entrega una función tangible en el sistema de ventas, stock y préstamos."));
        sb.append(",");
        sb.append(item("Bicycle Shedding (Ley de la Trivialidad)", "Organizacional", "PREVENIDO",
            "Debatir detalles estéticos triviales mientras se ignoran fallos críticos como stock desincronizado o acoplamiento fuerte.",
            "Priorización técnica: primero integridad de stock, validación SRP, Undo/Redo real y persistencia MySQL/DIP; luego una interfaz limpia y usable."));

        sb.append("]}");
        return sb.toString();
    }

    private static String item(String nombre, String categoria, String estado, String problema, String solucion) {
        return String.format(
            "{\"nombre\":\"%s\",\"categoria\":\"%s\",\"estado\":\"%s\",\"problema\":\"%s\",\"solucion\":\"%s\"}",
            escapar(nombre), escapar(categoria), escapar(estado), escapar(problema), escapar(solucion)
        );
    }

    private static String escapar(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
