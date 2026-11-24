package patrones.grasp.polimorfismo;

/**
 * SEMANA 17: GRASP Polimorfismo - Reglas específicas de préstamo y mora para Cuentos.
 */
public class RecursoCuento implements IRecursoBiblioteca {
    @Override
    public int getDiasPrestamoMaximo() {
        return 7; // Cuentos: 7 días de préstamo
    }

    @Override
    public double calcularMoraPorDia() {
        return 1.50; // S/ 1.50 por día de retraso
    }

    @Override
    public String getCategoriaTipo() {
        return "Cuento Infantil / Juvenil";
    }
}
