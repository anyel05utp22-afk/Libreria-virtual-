package patrones.creacionales.singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * SEMANA 6: Patrón Creacional Singleton
 * Garantiza una única instancia de conexión o gestor de base de datos para toda la aplicación.
 * Implementación segura para hilos (Double-Checked Locking).
 */
public class ConexionSingleton {
    private static volatile ConexionSingleton instancia;
    private Connection conexion;

    private static final String URL = "jdbc:mysql://localhost:3306/libreria_ElSaber?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private ConexionSingleton() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[Singleton] Conexión MySQL inicializada con éxito.");
        } catch (Exception e) {
            System.err.println("[Singleton] Error al inicializar conexión: " + e.getMessage());
        }
    }

    public static ConexionSingleton getInstancia() {
        if (instancia == null) {
            synchronized (ConexionSingleton.class) {
                if (instancia == null) {
                    instancia = new ConexionSingleton();
                }
            }
        }
        return instancia;
    }

    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("[Singleton] Error reconectando: " + e.getMessage());
        }
        return conexion;
    }
}

