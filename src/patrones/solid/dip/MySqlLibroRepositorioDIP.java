package patrones.solid.dip;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import patrones.creacionales.singleton.ConexionSingleton;
import patrones.estructurales.adapter.LegacyConexionAdapter;
import patrones.solid.srp.LibroSRP;

/**
 * SEMANA 3: DIP - Implementación concreta MySQL de IRepositorioDIP<LibroSRP>.
 * Utiliza ConexionSingleton (Semana 6) para lecturas y LegacyConexionAdapter (Semana 8)
 * para operaciones de escritura con procedimientos almacenados (CRL, Mlibro, Elibro).
 */
public class MySqlLibroRepositorioDIP implements IRepositorioDIP<LibroSRP> {
    private final ConexionSingleton db;
    private final LegacyConexionAdapter adapter;

    public MySqlLibroRepositorioDIP() {
        this.db = ConexionSingleton.getInstancia();
        this.adapter = new LegacyConexionAdapter();
        asegurarCatalogoSemillaEnBD();
    }

    /**
     * Si la tabla 'libro' tiene pocos registros iniciales, siembra el catálogo temático
     * sin duplicar títulos ya existentes para que todo libro visible en la web sea real en MySQL.
     */
    private void asegurarCatalogoSemillaEnBD() {
        Connection con = db.getConexion();
        if (con == null) return;
        try {
            Set<String> titulosExistentes = new HashSet<>();
            try (PreparedStatement ps = con.prepareStatement("SELECT LOWER(TRIM(titulo)) FROM libro");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    titulosExistentes.add(rs.getString(1));
                }
            }
            if (titulosExistentes.size() < 15) {
                for (LibroSRP seed : MemoriaLibroRepositorioDIP.obtenerCatalogoSemilla()) {
                    String clave = seed.getTitulo().trim().toLowerCase();
                    if (!titulosExistentes.contains(clave)) {
                        adapter.insertar(seed);
                        titulosExistentes.add(clave);
                    }
                }
                System.out.println("[DIP - MySQL] Catálogo inicial verificado/sembrado (" + titulosExistentes.size() + " títulos en BD).");
            }
        } catch (Exception e) {
            System.err.println("[DIP - MySQL] Aviso verificando catálogo semilla: " + e.getMessage());
        }
    }

    @Override
    public boolean guardar(LibroSRP entidad) {
        return guardarRetornandoId(entidad) > 0;
    }

    @Override
    public int guardarRetornandoId(LibroSRP entidad) {
        boolean ok = adapter.insertar(entidad);
        if (!ok) return -1;
        Connection con = db.getConexion();
        if (con == null) return -1;
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT MAX(id_libro) FROM libro")) {
            db.registrarConsulta();
            if (rs.next()) {
                int id = rs.getInt(1);
                entidad.setId(id);
                return id;
            }
        } catch (Exception e) {
            System.err.println("[DIP - MySQL] Error obteniendo ID generado: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean actualizar(LibroSRP entidad) {
        return adapter.actualizar(entidad);
    }

    @Override
    public LibroSRP buscarPorId(int id) {
        Connection con = db.getConexion();
        if (con == null) return null;
        String sql = "SELECT id_libro, autor, titulo, editorial, materia, precio, stock FROM libro WHERE id_libro = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            db.registrarConsulta();
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("[DIP - MySQL] Error buscando libro #" + id + ": " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<LibroSRP> listarTodos() {
        List<LibroSRP> lista = new ArrayList<>();
        Connection con = db.getConexion();
        if (con == null) return lista;
        String sql = "SELECT id_libro, autor, titulo, editorial, materia, precio, stock FROM libro ORDER BY id_libro ASC";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            db.registrarConsulta();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (Exception e) {
            System.err.println("[DIP - MySQL] Error listando libros: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean eliminar(int id) {
        return adapter.suprimir(id);
    }

    private LibroSRP mapear(ResultSet rs) throws Exception {
        return new LibroSRP(
            rs.getInt("id_libro"),
            rs.getString("autor"),
            rs.getString("titulo"),
            rs.getString("editorial"),
            rs.getString("materia"),
            rs.getDouble("precio"),
            rs.getInt("stock")
        );
    }

    @Override
    public String getNombreMotor() {
        return "MySqlLibroRepositorioDIP (MySQL 8.0 - libreria_ElSaber)";
    }
}
