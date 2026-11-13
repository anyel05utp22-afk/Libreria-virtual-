/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cuento;

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

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/libreria_ElSaber",
                "root",
                "root"
        );

        Statement st = con.createStatement();

        ResultSet rs = st.executeQuery("SELECT * FROM cuento");

        System.out.println("LISTA DE CUENTOS:");
        while (rs.next()) {
            System.out.println(
                rs.getInt("id_cuento") + "  " +
                rs.getString("autor") + "  " +
                rs.getString("titulo")
            );
        }

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("ERROR DE CONEXION EN SELECT");
    }
    }
}
