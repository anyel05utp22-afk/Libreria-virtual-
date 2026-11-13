/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cliente;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
public class Modificar {
    public static void main(String[] args) {
          try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/libreria_ElSaber", "root", "root");

            Statement obj = con.createStatement();

            String sql = "UPDATE cliente SET nombre='Carlos Lopez' WHERE id_cliente=1";
            obj.executeUpdate(sql);

            System.out.println("SE MODIFICO CORRECTAMENTE EN LA TABLA CLIENTE");

        } catch (Exception e) {
            System.out.println("ERROR DE CONEXION EN UPDATE");
        }
    }
   
}
