package patrones.grasp.polimorfismo;

public class RecursoRevista implements IRecursoBiblioteca {
    @Override
    public int getDiasPrestamoMaximo() {
        return 3; // Revistas: 3 días máximo por ser de alta rotación
    }

    @Override
    public double calcularMoraPorDia() {
        return 5.00; // S/ 5.00 por día de retraso
    }

    @Override
    public String getCategoriaTipo() {
        return "Revista Periódica";
    }
}

