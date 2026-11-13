/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Producto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 *
 * @author Home
 */
public class Registrar {
    public static void main(String[] args) {
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

    }
}
