package patrones.grasp.polimorfismo;

/**
 * SEMANA 17: Patrón GRASP - Polimorfismo (Polymorphism)
 * ¿Cómo manejar alternativas basadas en tipo? Asignando la responsabilidad del comportamiento
 * usando operaciones polimórficas en lugar de bloques switch / if-else anidados.
 */
public interface IRecursoBiblioteca {
    int getDiasPrestamoMaximo();
    double calcularMoraPorDia();
    String getCategoriaTipo();
}

