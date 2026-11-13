package patrones.estructurales.bridge;

public class FormatoDigitalEbook implements IFormatoBridge {
    @Override
    public String renderizarContenido(String titulo, String autor) {
        return "E-Book EPUB/PDF para lector digital: " + titulo;
    }

    @Override
    public double costoAdicionalFormato() {
        return 0.00; // Sin costo de papel
    }
}

