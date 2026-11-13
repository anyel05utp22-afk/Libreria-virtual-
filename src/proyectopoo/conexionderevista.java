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

public class conexionderevista {
  public static void main(String[] args) {
  try {
    // UPDATE
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/libreria_ElSaber", "root", "root");

    Statement obj = con.createStatement();

    String sql = "UPDATE revista SET marca='Marvel Nueva' WHERE id_revista=1";
    obj.executeUpdate(sql);

    System.out.println("SE MODIFICÓ CORRECTAMENTE EN LA TABLA REVISTA");

} catch (Exception e) {
    System.out.println("ERROR DE CONEXION");
}



try {
    // INSERT
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/libreria_ElSaber", "root", "root");

    Statement obj = con.createStatement();

    String sql = "INSERT INTO revista (marca, titulo, edicion, fecha_publicacion, precio, stock) "
            + "VALUES ('Marvel', 'Spiderman 2025', 12, '2024-11-22', 25.90, 50)";
    obj.executeUpdate(sql);

    System.out.println("SE INSERTÓ CORRECTAMENTE EN LA TABLA REVISTA");

} catch (Exception e) {
    System.out.println("ERROR DE CONEXION");
}



try {
    // DELETE
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/libreria_ElSaber", "root", "root");

    Statement obj = con.createStatement();

    String sql = "DELETE FROM revista WHERE id_revista=2";
    obj.executeUpdate(sql);

    System.out.println("SE ELIMINÓ CORRECTAMENTE EN LA TABLA REVISTA");

} catch (Exception e) {
    System.out.println("ERROR DE CONEXION");
}



try {
    // SELECT
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/libreria_ElSaber", "root", "root");

    Statement obj = con.createStatement();

    ResultSet re = obj.executeQuery("SELECT * FROM revista");

    while (re.next()) {
        System.out.println(
            re.getInt("id_revista") + " "
            + re.getString("marca") + " "
            + re.getString("titulo")
        );
    }

} catch (Exception e) {
    System.out.println("ERROR DE CONEXION");
}
  }
}