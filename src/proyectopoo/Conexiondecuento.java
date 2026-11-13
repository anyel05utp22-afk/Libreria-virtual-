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
/**
 *
 * @author Home
 */
public class Conexiondecuento {
    public static void main(String[] args) {
    try {

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/libreria_ElSaber",
                "root",
                "root"
        );

        Statement st = con.createStatement();

        String sql = "UPDATE cuento SET autor='Nuevo Autor' WHERE id_cuento=1";
        st.executeUpdate(sql);

        System.out.println("SE MODIFICÓ CORRECTAMENTE EN LA TABLA cuento");

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("ERROR DE CONEXION EN UPDATE");
    }

    // ---------------------- INSERT -------------------------
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

    // ---------------------- DELETE -------------------------
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

    // ---------------------- SELECT -------------------------
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

