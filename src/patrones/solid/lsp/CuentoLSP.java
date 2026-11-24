package patrones.solid.lsp;

/**
 * SEMANA 2: LSP - Subclase CuentoLSP sustituible transparentemente por PublicacionLSP.
 * Mapea directamente la tabla 'cuento' de MySQL (libreria_ElSaber).
 */
public class CuentoLSP extends PublicacionLSP {
    private String autor;
    private String editorial;
    private int edadRecomendada;
    private int numeroPaginas;

    public CuentoLSP(int id, String titulo, String autor, String editorial,
                     int edadRecomendada, int numeroPaginas, double precio, int stock) {
        super(id, titulo, precio, stock);
        this.autor = autor;
        this.editorial = editorial;
        this.edadRecomendada = edadRecomendada;
        this.numeroPaginas = numeroPaginas;
    }

    @Override
    public String getTipo() {
        return "Cuento";
    }

    @Override
    public String getFichaTecnica() {
        return String.format("Cuento: %s | Autor: %s | Editorial: %s | Edad: +%d años | %d págs.",
            titulo, autor, editorial, edadRecomendada, numeroPaginas);
    }

    public String getAutor() { return autor; }
    public String getEditorial() { return editorial; }
    public int getEdadRecomendada() { return edadRecomendada; }
    public int getNumeroPaginas() { return numeroPaginas; }
}
