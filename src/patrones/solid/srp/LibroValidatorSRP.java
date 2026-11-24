package patrones.solid.srp;

import java.util.ArrayList;
import java.util.List;

/**
 * SEMANA 1: SRP - Responsabilidad exclusiva de validación de reglas de negocio.
 * No accede a base de datos ni formatea comprobantes; solo valida integridad de LibroSRP.
 */
public class LibroValidatorSRP {

    public static boolean validar(LibroSRP libro) {
        return validarConErrores(libro).isEmpty();
    }

    public static List<String> validarConErrores(LibroSRP libro) {
        List<String> errores = new ArrayList<>();
        if (libro == null) {
            errores.add("El libro no puede ser nulo.");
            return errores;
        }
        if (libro.getTitulo() == null || libro.getTitulo().trim().length() < 2) {
            errores.add("El título es obligatorio y debe tener al menos 2 caracteres.");
        }
        if (libro.getAutor() == null || libro.getAutor().trim().length() < 2) {
            errores.add("El autor es obligatorio y debe tener al menos 2 caracteres.");
        }
        if (libro.getEditorial() == null || libro.getEditorial().trim().isEmpty()) {
            errores.add("La editorial es obligatoria.");
        }
        if (libro.getMateria() == null || libro.getMateria().trim().isEmpty()) {
            errores.add("La categoría / materia es obligatoria.");
        }
        if (libro.getPrecio() < 0 || libro.getPrecio() > 10000) {
            errores.add("El precio debe estar entre S/ 0.00 y S/ 10,000.00.");
        }
        if (libro.getStock() < 0 || libro.getStock() > 100000) {
            errores.add("El stock debe ser un número entero no negativo.");
        }
        return errores;
    }
}
