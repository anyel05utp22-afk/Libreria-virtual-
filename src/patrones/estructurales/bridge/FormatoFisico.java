package patrones.estructurales.bridge;

public class FormatoFisico implements IFormatoBridge {
    @Override
    public String renderizarContenido(String titulo, String autor) {
        return "Edición Impresa en Papel Couché: " + titulo + " por " + autor;
    }

    @Override
    public double costoAdicionalFormato() {
        return 12.00; // Costo de impresión y encuadernación
    }
}

