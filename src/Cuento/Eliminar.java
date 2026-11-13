/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cuento;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 *
 * @author Home
 */
public class Eliminar {
    public static void main(String[] args) {
         try {

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/libreria_ElSaber",
                "root",
                "root"
        );

        Statement st = con.createStatement();

        String sql = "DELETE FROM cuento WHERE id_cuento=5";
        st.executeUpdate(sql);

        System.out.println("SE ELIMINÓ CORRECTAMENTE EN LA TABLA cuento");

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("ERROR DE CONEXION EN DELETE");
    }
    }
}
