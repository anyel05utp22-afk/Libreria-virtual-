/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Libro;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Home
 */
public class Mostrar {
    public static void main(String[] args) {
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

