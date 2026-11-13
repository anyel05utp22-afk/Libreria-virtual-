/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyectopoo;


import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ConexionLibro {
   
   Connection cn;
   PreparedStatement ps;
   ResultSet rs;
   public ConexionLibro(){
       try{
           Class.forName("com.mysql.cj.jdbc.Driver");
           cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/libreria_ElSaber",
                   "root",
                   "root");
           System.out.print("Conectado BD");
       }catch(Exception e){
           System.out.print("Error al Conectar BD");
       }
   }
   
   public Connection getConnection(){
       return cn;
   }
   
   public int Rlibro(String autor,String titulo,String editorial, String materia, float precio, int stock){
       int res=0;
       Connection cn=null;
       CallableStatement cs=null;
       try{
           cn = getConnection();
           cs=cn.prepareCall("{ CALL CRL(?,?,?,?,?,?)}");
           cs.setString(1, autor);
           cs.setString(2, titulo);
           cs.setString(3, editorial);
           cs.setString(4, materia);
           cs.setFloat(5,precio);
           cs.setInt(6, stock);
           res=cs.executeUpdate();

           System.out.print("Usuario registrado correctamente");
       }catch(Exception e){
           System.out.print("Error al registrar");
       }finally {
        try {
            if(cs != null) cs.close();
            if(cn != null) cn.close();  // ESTO era lo que faltaba
        } catch (Exception ex) {
            System.out.println("Error cerrando conexión: " + ex.getMessage());
        }
    }
       return res;
   }
     public int Mlibro(int idlibro,String autor,String titulo,String editorial, String materia, float precio, int stock) {
        int res=0;
        
        try {
            ps=cn.prepareStatement("UPDATE libro SET autor=?, titulo=?, editorial=?, materia=?, precio=?, stock=? where id_libro=?");
            ps.setString(1, autor);
            ps.setString(2, titulo);
            ps.setString(3, editorial);
            ps.setString(4, materia);
            ps.setFloat(5,precio);
            ps.setInt(6, stock);
            ps.setInt(7, idlibro);
            res=ps.executeUpdate();
            ps.close();
            System.out.println("Registro Modificado");
        } catch(Exception e) {
            System.out.println("Error al modificar");
        }
        return res;
    }
     public int Elibro(int idlibro) {
        int res=0;
        
        try {
            ps=cn.prepareStatement("DELETE from libro where id_libro=?");
            ps.setInt(1, idlibro);
            res=ps.executeUpdate();
            ps.close();
            System.out.println("Registro Eliminado");
        } catch(Exception e) {
            System.out.println("Error al eliminar");
        }
        return res;
    }
}

