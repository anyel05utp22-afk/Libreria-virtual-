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
public class Registrar {
    public static void main(String[] args) {
         try {

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/libreria_ElSaber",
                "root",
                "root"
        );

        Statement st = con.createStatement();

        String sql = "INSERT INTO cuento (autor, titulo, editorial, edad_recomendada, numero_paginas, precio, stock) " +
                     "VALUES ('Hans Christian Andersen', 'El Patito Feo', 'Planeta Kids', 5, 32, 18.50, 40)";
        st.executeUpdate(sql);

        System.out.println("SE INSERTÓ CORRECTAMENTE EN LA TABLA cuento");

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("ERROR DE CONEXION EN INSERT");
    }
    }
}
