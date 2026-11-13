/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectopoo;
import java.sql.*;
/**
 *
 * @author Home
 */
public class ConexionProducto {
    Connection cn;
   PreparedStatement ps;
   ResultSet rs;
   public ConexionProducto() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            cn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/libreria_ElSaber",
                    "root",
                    "root"
            );
            System.out.println("Conectado BD");
        } catch (Exception e) {
            System.out.println("Error al conectar BD");
        }
    }

    public Connection getConnection() {
        return cn;
    }

    // --------------------------------------
    // REGISTRAR PRODUCTO
    // --------------------------------------
    public int Rproducto(Integer id_cuento, Integer id_revista, Integer id_libro) {

        int res = 0;

        try {
            ps = cn.prepareStatement(
                 "INSERT INTO producto (id_cuento, id_revista, id_libro) VALUES (?, ?, ?)"
            );

            // Estos valores pueden ser NULL si no se usa ese tipo de producto
            if (id_cuento != null) ps.setInt(1, id_cuento);
            else ps.setNull(1, java.sql.Types.INTEGER);

            if (id_revista != null) ps.setInt(2, id_revista);
            else ps.setNull(2, java.sql.Types.INTEGER);

            if (id_libro != null) ps.setInt(3, id_libro);
            else ps.setNull(3, java.sql.Types.INTEGER);

            res = ps.executeUpdate();
            ps.close();

            System.out.println("Producto registrado correctamente");

        } catch (Exception e) {
            System.out.println("Error al registrar producto: " + e.getMessage());
        }

        return res;
    }

    // --------------------------------------
    // MODIFICAR PRODUCTO
    // --------------------------------------
    public int Mproducto(int id_producto, Integer id_cuento, Integer id_revista, Integer id_libro) {

        int res = 0;

        try {
            ps = cn.prepareStatement(
                "UPDATE producto SET id_cuento=?, id_revista=?, id_libro=? WHERE id_producto=?"
            );

            if (id_cuento != null) ps.setInt(1, id_cuento);
            else ps.setNull(1, java.sql.Types.INTEGER);

            if (id_revista != null) ps.setInt(2, id_revista);
            else ps.setNull(2, java.sql.Types.INTEGER);

            if (id_libro != null) ps.setInt(3, id_libro);
            else ps.setNull(3, java.sql.Types.INTEGER);

            ps.setInt(4, id_producto);

            res = ps.executeUpdate();
            ps.close();

            System.out.println("Producto modificado");

        } catch (Exception e) {
            System.out.println("Error al modificar producto: " + e.getMessage());
        }

        return res;
    }

    public int Eproducto(int id_producto) {

        int res = 0;

        try {
            ps = cn.prepareStatement("DELETE FROM producto WHERE id_producto=?");
            ps.setInt(1, id_producto);

            res = ps.executeUpdate();
            ps.close();

            System.out.println("Producto eliminado");

        } catch (Exception e) {
            System.out.println("Error al eliminar producto: " + e.getMessage());
        }

        return res;
    }
}

