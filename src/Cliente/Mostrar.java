/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cliente;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
public class Mostrar {
    public static void main(String[] args) {
           try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/libreria_ElSaber", "root", "root");

            Statement obj = con.createStatement();

            ResultSet re = obj.executeQuery("SELECT * FROM cliente");

            while (re.next()) {
                System.out.println(
                    re.getInt("id_cliente") + " "
                    + re.getString("nombre") + " "
                    + re.getString("dni") + " "
                    + re.getString("direccion") + " "
                    + re.getString("telefono")
                );
            }

        } catch (Exception e) {
            System.out.println("ERROR DE CONEXION EN SELECT");
        }

    }
}
    

