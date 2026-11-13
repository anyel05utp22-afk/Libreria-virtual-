package proyectopoo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ConexionRevista {
    
    Connection cn;
    PreparedStatement ps;
    ResultSet rs;
    
    public ConexionRevista() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            cn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/libreria_ElSaber",
                    "root",
                    "root"
            );
            System.out.println("Conectado BD");
        } catch (Exception e) {
            System.out.println("Error al Conectar BD");
        }
    }

    public Connection getConnection() {
        return cn;
    }

    // ========== REGISTRAR REVISTA (IGUAL QUE Rlibro, PERO CON OTRA SP) ==========
    public int Rrevista(String marca, String titulo, int edicion, String fecha_publicacion, double precio, int stock) {
        int res = 0;
        Connection cn = null;
        CallableStatement cs = null;
        
        try {
            cn = getConnection();
            cs = cn.prepareCall("{ CALL CRR(?,?,?,?,?,?) }");  // SP DE REVISTA

            cs.setString(1, marca);
            cs.setString(2, titulo);
            cs.setInt(3, edicion);
            cs.setString(4, fecha_publicacion);
            cs.setDouble(5, precio);
            cs.setInt(6, stock);

            res = cs.executeUpdate();
            System.out.println("Revista registrada correctamente");

        } catch (Exception e) {
            System.out.println("Error al registrar revista: " + e.getMessage());
        } finally {
            try {
                if (cs != null) cs.close();
                // NO CERRAR cn (igual que tu clase libro)
            } catch (Exception ex) {
                System.out.println("Error cerrando recursos: " + ex.getMessage());
            }
        }
        
        return res;
    }

    // ========== MODIFICAR REVISTA (IGUAL QUE Mlibro) ==========
    public int Mrevista(int id_revista, String marca, String titulo, int edicion, 
                        String fecha_publicacion, double precio, int stock) {
        int res = 0;

        try {
            ps = cn.prepareStatement(
                "UPDATE revista SET marca=?, titulo=?, edicion=?, fecha_publicacion=?, precio=?, stock=? WHERE id_revista=?"
            );

            ps.setString(1, marca);
            ps.setString(2, titulo);
            ps.setInt(3, edicion);
            ps.setString(4, fecha_publicacion);
            ps.setDouble(5, precio);
            ps.setInt(6, stock);
            ps.setInt(7, id_revista);

            res = ps.executeUpdate();
            ps.close();
            System.out.println("Revista modificada correctamente");

        } catch (Exception e) {
            System.out.println("Error al modificar revista: " + e.getMessage());
        }

        return res;
    }

    // ========== ELIMINAR REVISTA (IGUAL QUE Elibro) ==========
    public int Erevista(int id_revista) {
        int res = 0;

        try {
            ps = cn.prepareStatement("DELETE FROM revista WHERE id_revista=?");
            ps.setInt(1, id_revista);

            res = ps.executeUpdate();
            ps.close();
            System.out.println("Revista eliminada correctamente");

        } catch (Exception e) {
            System.out.println("Error al eliminar revista: " + e.getMessage());
        }

        return res;
    }
}
