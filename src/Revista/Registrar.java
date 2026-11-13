/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Revista;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Registrar {
    public static void main(String[] args) {
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

    }
}
