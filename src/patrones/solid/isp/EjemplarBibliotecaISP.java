package patrones.solid.isp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * SEMANA 3: ISP - EjemplarBibliotecaISP implementa únicamente IPrestable.
 * Representa un ejemplar destinado al préstamo bibliotecario (con DNI, fechas y devolución)
 * sin verse forzado a implementar IDigital ni lógica de venta comercial.
 */
public class EjemplarBibliotecaISP implements IPrestable {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final int idPrestamo;
    private final int idPublicacion;
    private final String titulo;
    private final String tipoRecurso;
    private final int diasPlazo;
    private final double moraPorDia;

    private String dniCliente;
    private String nombreCliente;
    private LocalDate fechaPrestamo;
    private LocalDate fechaLimite;
    private boolean prestado;

    public EjemplarBibliotecaISP(int idPrestamo, int idPublicacion, String titulo,
                                 String tipoRecurso, int diasPlazo, double moraPorDia) {
        this.idPrestamo = idPrestamo;
        this.idPublicacion = idPublicacion;
        this.titulo = titulo;
        this.tipoRecurso = tipoRecurso;
        this.diasPlazo = diasPlazo;
        this.moraPorDia = moraPorDia;
        this.prestado = false;
    }

    public boolean prestarConNombre(String dniCliente, String nombreCliente) {
        this.nombreCliente = (nombreCliente == null || nombreCliente.isBlank()) ? "Lector Biblioteca" : nombreCliente;
        return prestar(dniCliente);
    }

    @Override
    public boolean prestar(String dniCliente) {
        if (prestado || dniCliente == null || dniCliente.trim().isEmpty()) {
            return false;
        }
        this.dniCliente = dniCliente.trim();
        this.fechaPrestamo = LocalDate.now();
        this.fechaLimite = fechaPrestamo.plusDays(diasPlazo);
        this.prestado = true;
        return true;
    }

    @Override
    public void devolver() {
        this.prestado = false;
    }

    @Override
    public boolean estaPrestado() {
        return prestado;
    }

    public double calcularMoraEstimada(int diasRetraso) {
        return Math.max(0, diasRetraso) * moraPorDia;
    }

    public int getIdPrestamo() { return idPrestamo; }
    public int getIdPublicacion() { return idPublicacion; }
    public String getTitulo() { return titulo; }
    public String getTipoRecurso() { return tipoRecurso; }
    public int getDiasPlazo() { return diasPlazo; }
    public double getMoraPorDia() { return moraPorDia; }
    public String getDniCliente() { return dniCliente; }
    public String getNombreCliente() { return nombreCliente; }
    public String getFechaPrestamoStr() { return fechaPrestamo != null ? fechaPrestamo.format(DATE_FMT) : ""; }
    public String getFechaLimiteStr() { return fechaLimite != null ? fechaLimite.format(DATE_FMT) : ""; }
}
