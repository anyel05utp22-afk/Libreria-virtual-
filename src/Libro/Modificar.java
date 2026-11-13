/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Libro;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
/**
 *
 * @author Home
 */
public class Modificar {
    public static void main(String[] args) {
          try {
    // UPDATE
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/libreria_ElSaber", "root", "root");

    Statement obj = con.createStatement();

    String sql = "UPDATE revista SET marca='pocoyo' WHERE id_revista=4";
    obj.executeUpdate(sql);

    System.out.println("SE MODIFICÓ CORRECTAMENTE EN LA TABLA REVISTA");

} catch (Exception e) {
    System.out.println("ERROR DE CONEXION");
}
    }
}
