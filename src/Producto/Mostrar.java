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
public class Mostrar {
    public static void main(String[] args) {
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

    }
}
