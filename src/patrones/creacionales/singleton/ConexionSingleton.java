package patrones.creacionales.singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SEMANA 6: Patrón Creacional Singleton
 * Garantiza una única instancia de gestor de conexión a MySQL (libreria_ElSaber)
 * mediante Double-Checked Locking, con reconexión automática, soporte de credenciales
 * alternativas ('root', 'admin', '') y telemetría de uso en tiempo real.
 */
public class ConexionSingleton {
    private static volatile ConexionSingleton instancia;
    private Connection conexion;
    private boolean conectado;
    private String credencialUsada = "ninguna";
    private final AtomicInteger consultasEjecutadas = new AtomicInteger(0);
    private final AtomicInteger reconexiones = new AtomicInteger(0);

    private static final String URL = "jdbc:mysql://localhost:3306/libreria_ElSaber?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String[] PASSWORDS_SOPORTADOS = {"root", "admin", ""};

    private ConexionSingleton() {
        inicializarConexion();
    }

    private synchronized void inicializarConexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[Singleton] Driver MySQL no encontrado: " + e.getMessage());
            conectado = false;
            return;
        }
        for (String pwd : PASSWORDS_SOPORTADOS) {
            try {
                this.conexion = DriverManager.getConnection(URL, USER, pwd);
                this.conectado = true;
                this.credencialUsada = pwd.isEmpty() ? "(vacía)" : "root/***";
                System.out.println("[Singleton] Conexión MySQL inicializada con éxito (instancia #" + System.identityHashCode(this) + ").");
                return;
            } catch (SQLException ignored) {
                // Intentar siguiente credencial soportada
            }
        }
        this.conectado = false;
        System.err.println("[Singleton] No se pudo conectar a MySQL; se activará respaldo DIP en memoria.");
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

    public synchronized Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed() || !conexion.isValid(2)) {
                reconexiones.incrementAndGet();
                inicializarConexion();
            }
        } catch (SQLException e) {
            reconexiones.incrementAndGet();
            inicializarConexion();
        }
        return conexion;
    }

    public boolean estaConectado() {
        try {
            return conectado && conexion != null && !conexion.isClosed() && conexion.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    public void registrarConsulta() {
        consultasEjecutadas.incrementAndGet();
    }

    public int getConsultasEjecutadas() {
        return consultasEjecutadas.get();
    }

    public int getReconexiones() {
        return reconexiones.get();
    }

    public int getHashInstancia() {
        return System.identityHashCode(this);
    }

    public String getResumenEstado() {
        return estaConectado()
            ? "Conectado a MySQL 8.0 (" + credencialUsada + ") | Instancia #" + getHashInstancia()
            : "Modo Respaldo Local (MySQL no disponible) | Instancia #" + getHashInstancia();
    }
}
