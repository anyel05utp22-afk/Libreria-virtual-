package patrones.solid.dip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import patrones.solid.srp.LibroSRP;

/**
 * SEMANA 3: DIP - Implementación en memoria de IRepositorioDIP<LibroSRP>.
 * Sirve como almacenamiento autónomo de respaldo cuando MySQL no está disponible
 * y como fuente de catálogo base (semilla) de la Librería Virtual El Saber.
 */
public class MemoriaLibroRepositorioDIP implements IRepositorioDIP<LibroSRP> {
    private final Map<Integer, LibroSRP> almacenamiento = Collections.synchronizedMap(new LinkedHashMap<>());
    private final AtomicInteger secuenciaId = new AtomicInteger(500);

    public MemoriaLibroRepositorioDIP() {
        for (LibroSRP libro : obtenerCatalogoSemilla()) {
            almacenamiento.put(libro.getId(), clonar(libro));
        }
    }

    public static List<LibroSRP> obtenerCatalogoSemilla() {
        List<LibroSRP> semilla = new ArrayList<>();
        // 1. Recomendados de amor
        semilla.add(new LibroSRP(101, "Ana González", "El silencio entre tú y yo", "Planeta", "Recomendados de amor", 38.00, 5));
        semilla.add(new LibroSRP(102, "Florencia Bonelli", "El hechizo del agua", "Suma", "Recomendados de amor", 45.00, 0));
        semilla.add(new LibroSRP(103, "Gioconda Belli", "Luciérnagas", "Seix Barral", "Recomendados de amor", 32.00, 2));
        semilla.add(new LibroSRP(104, "Inma Rubiales", "Todos los lugares que mantuvimos en secreto", "Planeta", "Recomendados de amor", 40.00, 0));
        semilla.add(new LibroSRP(105, "Pedro Saborido", "Una historia del amor", "Planeta", "Recomendados de amor", 35.00, 3));
        semilla.add(new LibroSRP(106, "Gabriel Rolón", "Historias de diván", "Planeta", "Recomendados de amor", 48.00, 4));

        // 2. Novelas y cuentos para las vacaciones
        semilla.add(new LibroSRP(201, "Varios Autores", "El duelo de honor", "Seix Barral", "Novelas y cuentos para las vacaciones", 28.00, 0));
        semilla.add(new LibroSRP(202, "Liliana Heker", "Cuentos reunidos", "Alfaguara", "Novelas y cuentos para las vacaciones", 30.00, 6));
        semilla.add(new LibroSRP(203, "Manuel Mujica Láinez", "Aquí vivieron", "Sudamericana", "Novelas y cuentos para las vacaciones", 34.00, 2));
        semilla.add(new LibroSRP(204, "Diego M. Zigiotto", "365 días en Buenos Aires", "Ediciones B", "Novelas y cuentos para las vacaciones", 42.00, 0));
        semilla.add(new LibroSRP(205, "Mariana Enríquez", "Las cosas que perdimos en el fuego", "Anagrama", "Novelas y cuentos para las vacaciones", 36.00, 5));
        semilla.add(new LibroSRP(206, "Isabel Allende", "Cuentos de Eva Luna", "Debolsillo", "Novelas y cuentos para las vacaciones", 39.00, 7));

        // 3. Clásicos
        semilla.add(new LibroSRP(301, "Gabriel García Márquez", "En agosto nos vemos", "Random House", "Clásicos", 49.00, 0));
        semilla.add(new LibroSRP(302, "Gabriel García Márquez", "Crónica de una muerte anunciada", "Debolsillo", "Clásicos", 35.00, 8));
        semilla.add(new LibroSRP(303, "Alfonsina Storni", "Antología poética", "Losada", "Clásicos", 29.00, 3));
        semilla.add(new LibroSRP(304, "Tomás Eloy Martínez", "Ficciones verdaderas", "Planeta", "Clásicos", 38.00, 5));
        semilla.add(new LibroSRP(305, "Julio Cortázar", "Bestiario", "Alfaguara", "Clásicos", 42.00, 7));
        semilla.add(new LibroSRP(306, "Julio Cortázar", "Cortázar de la A a la Z", "Alfaguara", "Clásicos", 52.00, 4));

        // 4. Filosofía, Educación, Ciencia y Tecnología
        semilla.add(new LibroSRP(401, "Han Kang", "Imposible decir adiós", "Random House", "Filosofía, Educación, Ciencia y Tecnología", 46.00, 4));
        semilla.add(new LibroSRP(402, "Mariana Enríquez", "Visión nocturna", "Ampersand", "Filosofía, Educación, Ciencia y Tecnología", 38.00, 6));
        semilla.add(new LibroSRP(403, "Slavoj Žižek", "Mundo loco", "Akal", "Filosofía, Educación, Ciencia y Tecnología", 44.00, 0));
        semilla.add(new LibroSRP(404, "Ali Smith", "Biblioteca Pública", "Nórdica", "Filosofía, Educación, Ciencia y Tecnología", 36.00, 5));
        semilla.add(new LibroSRP(405, "Yuval Noah Harari", "Nexus", "Debate", "Filosofía, Educación, Ciencia y Tecnología", 65.00, 10));
        semilla.add(new LibroSRP(406, "Yanis Varoufakis", "Tecnofeudalismo", "Deusto", "Filosofía, Educación, Ciencia y Tecnología", 54.00, 4));
        semilla.add(new LibroSRP(407, "Ra-Ma Editorial", "Inteligencia artificial para pintores y artistas", "Ra-Ma", "Filosofía, Educación, Ciencia y Tecnología", 59.00, 3));
        return semilla;
    }

    private LibroSRP clonar(LibroSRP l) {
        return new LibroSRP(l.getId(), l.getAutor(), l.getTitulo(), l.getEditorial(), l.getMateria(), l.getPrecio(), l.getStock());
    }

    @Override
    public boolean guardar(LibroSRP entidad) {
        return guardarRetornandoId(entidad) > 0;
    }

    @Override
    public int guardarRetornandoId(LibroSRP entidad) {
        if (entidad == null) return -1;
        int id = entidad.getId() > 0 && !almacenamiento.containsKey(entidad.getId())
            ? entidad.getId()
            : secuenciaId.incrementAndGet();
        LibroSRP copia = new LibroSRP(id, entidad.getAutor(), entidad.getTitulo(),
            entidad.getEditorial(), entidad.getMateria(), entidad.getPrecio(), entidad.getStock());
        almacenamiento.put(id, copia);
        entidad.setId(id);
        return id;
    }

    @Override
    public boolean actualizar(LibroSRP entidad) {
        if (entidad == null || !almacenamiento.containsKey(entidad.getId())) return false;
        almacenamiento.put(entidad.getId(), clonar(entidad));
        return true;
    }

    @Override
    public LibroSRP buscarPorId(int id) {
        LibroSRP encontrado = almacenamiento.get(id);
        return encontrado != null ? clonar(encontrado) : null;
    }

    @Override
    public List<LibroSRP> listarTodos() {
        List<LibroSRP> copia = new ArrayList<>();
        synchronized (almacenamiento) {
            for (LibroSRP l : almacenamiento.values()) {
                copia.add(clonar(l));
            }
        }
        return copia;
    }

    @Override
    public boolean eliminar(int id) {
        return almacenamiento.remove(id) != null;
    }

    @Override
    public String getNombreMotor() {
        return "MemoriaLibroRepositorioDIP (Respaldo En Memoria)";
    }
}
