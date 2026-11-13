package patrones.estructurales.facade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import patrones.creacionales.singleton.ConexionSingleton;
import patrones.estructurales.adapter.LegacyConexionAdapter;
import patrones.solid.srp.LibroSRP;

/**
 * SEMANA 8: Patrón Estructural Facade
 * Proporciona una interfaz unificada y simple a un subsistema complejo (base de datos, adaptadores, cálculos).
 */
public class LibreriaFacade {
    private final LegacyConexionAdapter adapter;
    private final ConexionSingleton db;

    public LibreriaFacade() {
        this.adapter = new LegacyConexionAdapter();
        this.db = ConexionSingleton.getInstancia();
    }

    public List<LibroSRP> listarLibros() {
        List<LibroSRP> lista = new ArrayList<>();
        String sql = "SELECT id_libro, autor, titulo, editorial, materia, precio, stock FROM libro ORDER BY id_libro ASC";
        try (Connection con = db.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new LibroSRP(
                    rs.getInt("id_libro"),
                    rs.getString("autor"),
                    rs.getString("titulo"),
                    rs.getString("editorial"),
                    rs.getString("materia"),
                    rs.getDouble("precio"),
                    rs.getInt("stock")
                ));
            }
        } catch (Exception e) {
            System.err.println("[Facade] Error listando libros: " + e.getMessage());
        }
        return lista;
    }

    public boolean registrarLibro(String autor, String titulo, String editorial, String materia, double precio, int stock) {
        LibroSRP l = new LibroSRP(0, autor, titulo, editorial, materia, precio, stock);
        return adapter.insertar(l);
    }

    public boolean actualizarLibro(int id, String autor, String titulo, String editorial, String materia, double precio, int stock) {
        LibroSRP l = new LibroSRP(id, autor, titulo, editorial, materia, precio, stock);
        return adapter.actualizar(l);
    }

    public boolean eliminarLibro(int id) {
        return adapter.suprimir(id);
    }
}

