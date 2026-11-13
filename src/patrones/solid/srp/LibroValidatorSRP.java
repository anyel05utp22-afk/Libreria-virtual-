package patrones.solid.srp;

/**
 * SEMANA 1: SRP - Responsabilidad exclusiva de validación de reglas de negocio
 */
public class LibroValidatorSRP {
    public static boolean validar(LibroSRP libro) {
        if (libro == null) return false;
        if (libro.getTitulo() == null || libro.getTitulo().trim().isEmpty()) return false;
        if (libro.getAutor() == null || libro.getAutor().trim().isEmpty()) return false;
        if (libro.getPrecio() < 0) return false;
        if (libro.getStock() < 0) return false;
        return true;
    }
}

