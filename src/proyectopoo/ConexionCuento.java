/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectopoo;
import java.sql.*;
/**
 *
 * @author Home
 */
public class ConexionCuento {
   Connection cn;
   PreparedStatement ps;
   ResultSet rs;

   public ConexionCuento() {
       try {
           Class.forName("com.mysql.cj.jdbc.Driver");
           cn = DriverManager.getConnection(
                   "jdbc:mysql://localhost:3306/libreria_ElSaber",
                   "root",
                   "root"
           );
           System.out.println("Conectado BD");
       } catch(Exception e) {
           System.out.println("Error al conectar BD");
       }
   }
   
   public Connection getConnection() {
       return cn;
   }

   // --------------------------------------
   // REGISTRAR CUENTO
   // --------------------------------------
   public int Rcuento(String autor, String titulo, String editorial,
                      int edad_recomendada, int numero_paginas,
                      float precio, int stock) {

       int res = 0;
       Connection cn = null;
       CallableStatement cs = null;

       try {
           cn = getConnection();
           cs = cn.prepareCall("{ CALL CRC(?,?,?,?,?,?,?) }"); 
           
           cs.setString(1, autor);
           cs.setString(2, titulo);
           cs.setString(3, editorial);
           cs.setInt(4, edad_recomendada);
           cs.setInt(5, numero_paginas);
           cs.setFloat(6, precio);
           cs.setInt(7, stock);

           res = cs.executeUpdate();
           System.out.println("Cuento registrado correctamente");

       } catch(Exception e) {
           System.out.println("Error al registrar cuento");
       } finally {
           try {
               if(cs != null) cs.close();
               if(cn != null) cn.close();
           } catch(Exception ex) {
               System.out.println("Error cerrando conexion: " + ex.getMessage());
           }
       }
       return res;
   }

   // --------------------------------------
   // MODIFICAR CUENTO
   // --------------------------------------
   public int Mcuento(int idcuento, String autor, String titulo, String editorial,
                      int edad_recomendada, int numero_paginas,
                      float precio, int stock) {

       int res = 0;

       try {
           ps = cn.prepareStatement(
                "UPDATE cuento SET autor=?, titulo=?, editorial=?, edad_recomendada=?, numero_paginas=?, precio=?, stock=? WHERE id_cuento=?"
           );

           ps.setString(1, autor);
           ps.setString(2, titulo);
           ps.setString(3, editorial);
           ps.setInt(4, edad_recomendada);
           ps.setInt(5, numero_paginas);
           ps.setFloat(6, precio);
           ps.setInt(7, stock);
           ps.setInt(8, idcuento);

           res = ps.executeUpdate();
           ps.close();

           System.out.println("Registro modificado");

       } catch(Exception e) {
           System.out.println("Error al modificar cuento");
       }

       return res;
   }

   // --------------------------------------
   // ELIMINAR CUENTO
   // --------------------------------------
   public int Ecuento(int idcuento) {
       int res = 0;

       try {
           ps = cn.prepareStatement("DELETE FROM cuento WHERE id_cuento=?");
           ps.setInt(1, idcuento);

           res = ps.executeUpdate();
           ps.close();

           System.out.println("Registro eliminado");

       } catch(Exception e) {
           System.out.println("Error al eliminar cuento");
       }

       return res;
   }
}

