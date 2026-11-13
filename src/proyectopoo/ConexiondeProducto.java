/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectopoo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class ConexiondeProducto {
    public static void main(String[] args) {
          try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/libreria_ElSaber", "root", "root");

            Statement obj = con.createStatement();

            String sql = "UPDATE producto SET id_libro = 3 WHERE id_producto = 1";
            obj.executeUpdate(sql);

            System.out.println("SE MODIFICO CORRECTAMENTE EN LA TABLA PRODUCTO");

        } catch (Exception e) {
            System.out.println("ERROR DE CONEXION EN UPDATE");
        }



        // ============================================================
        // INSERT
        // ============================================================
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/libreria_ElSaber", "root", "root");

            Statement obj = con.createStatement();

            String sql = "INSERT INTO producto (id_cuento, id_revista, id_libro) "
                    + "VALUES (1, NULL, NULL)";
            obj.executeUpdate(sql);

            System.out.println("SE INSERTO CORRECTAMENTE EN LA TABLA PRODUCTO");

        } catch (Exception e) {
            System.out.println("ERROR DE CONEXION EN INSERT");
        }



        // ============================================================
        // DELETE
        // ============================================================
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/libreria_ElSaber", "root", "root");

            Statement obj = con.createStatement();

            String sql = "DELETE FROM producto WHERE id_producto = 2";
            obj.executeUpdate(sql);

            System.out.println("SE ELIMINO CORRECTAMENTE EN LA TABLA PRODUCTO");

        } catch (Exception e) {
            System.out.println("ERROR DE CONEXION EN DELETE");
        }



        // ============================================================
        // SELECT
        // ============================================================
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/libreria_ElSaber", "root", "root");

            Statement obj = con.createStatement();

            ResultSet re = obj.executeQuery("SELECT * FROM producto");

            while (re.next()) {
                System.out.println(
                        re.getInt("id_producto") + "  "
                        + re.getString("id_cuento") + "  "
                        + re.getString("id_revista") + "  "
                        + re.getString("id_libro")
                );
            }

        } catch (Exception e) {
            System.out.println("ERROR DE CONEXION EN SELECT");
        }

    }
}

    

