package patrones.grasp.polimorfismo;

public class RecursoLibro implements IRecursoBiblioteca {
    @Override
    public int getDiasPrestamoMaximo() {
        return 14; // Libros: 14 días
    }

    @Override
    public double calcularMoraPorDia() {
        return 2.50; // S/ 2.50 por día de retraso
    }

    @Override
    public String getCategoriaTipo() {
        return "Libro Especializado";
    }
}

