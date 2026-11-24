package patrones.grasp.polimorfismo;

/**
 * SEMANA 17: Patrón GRASP - Polimorfismo (Polymorphism)
 * Resuelve reglas variables según tipo de publicación (Libro, Cuento, Revista)
 * mediante despacho polimórfico en lugar de condicionales if/else dispersos.
 */
public interface IRecursoBiblioteca {
    int getDiasPrestamoMaximo();
    double calcularMoraPorDia();
    String getCategoriaTipo();

    default double calcularMoraTotal(int diasRetraso) {
        return Math.max(0, diasRetraso) * calcularMoraPorDia();
    }

    static IRecursoBiblioteca paraTipo(String tipo) {
        if (tipo == null) return new RecursoLibro();
        String t = tipo.trim().toLowerCase();
        if (t.contains("revista")) return new RecursoRevista();
        if (t.contains("cuento")) return new RecursoCuento();
        return new RecursoLibro();
    }
}
