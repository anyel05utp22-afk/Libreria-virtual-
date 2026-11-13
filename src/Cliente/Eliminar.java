/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cliente;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
public class Eliminar {
    public static void main(String[] args) {
         try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/libreria_ElSaber", "root", "root");

            Statement obj = con.createStatement();

            String sql = "DELETE FROM cliente WHERE id_cliente=2";
            obj.executeUpdate(sql);

            System.out.println("SE ELIMINO CORRECTAMENTE EN LA TABLA CLIENTE");

        } catch (Exception e) {
            System.out.println("ERROR DE CONEXION EN DELETE");
        }
    }
}
