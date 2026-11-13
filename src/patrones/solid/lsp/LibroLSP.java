package patrones.solid.lsp;

public class LibroLSP extends PublicacionLSP {
    private String autor;
    private String editorial;
    private String materia;

    public LibroLSP(int id, String titulo, String autor, String editorial, String materia, double precio, int stock) {
        super(id, titulo, precio, stock);
        this.autor = autor;
        this.editorial = editorial;
        this.materia = materia;
    }

    @Override
    public String getTipo() {
        return "Libro";
    }

    @Override
    public String getFichaTecnica() {
        return String.format("Libro: %s | Autor: %s | Editorial: %s | Materia: %s",
            titulo, autor, editorial, materia);
    }

    public String getAutor() { return autor; }
    public String getEditorial() { return editorial; }
    public String getMateria() { return materia; }
}

