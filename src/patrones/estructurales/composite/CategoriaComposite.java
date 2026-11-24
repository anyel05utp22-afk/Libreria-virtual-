package patrones.estructurales.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * SEMANA 9: Composite - Nodo Compuesto (CategoriaComposite)
 * Agrupa subcategorías y hojas (ItemCatalogoLeaf) permitiendo cálculos recursivos
 * de cantidad de títulos, unidades en stock y valorización total del inventario.
 */
public class CategoriaComposite implements ICatalogoComponent {
    private final String nombre;
    private final List<ICatalogoComponent> componentes = new ArrayList<>();

    public CategoriaComposite(String nombre) {
        this.nombre = nombre;
    }

    public void agregar(ICatalogoComponent comp) {
        if (comp != null) componentes.add(comp);
    }

    public void remover(ICatalogoComponent comp) {
        componentes.remove(comp);
    }

    public List<ICatalogoComponent> getComponentes() {
        return componentes;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public void mostrarJerarquia(String prefijo) {
        System.out.println(prefijo + "+ [Categoría] " + nombre + " (" + contarItems() + " elementos, stock: " + contarStockTotal() + ")");
        for (ICatalogoComponent c : componentes) {
            c.mostrarJerarquia(prefijo + "  ");
        }
    }

    @Override
    public int contarItems() {
        int total = 0;
        for (ICatalogoComponent c : componentes) {
            total += c.contarItems();
        }
        return total;
    }

    @Override
    public int contarStockTotal() {
        int total = 0;
        for (ICatalogoComponent c : componentes) {
            total += c.contarStockTotal();
        }
        return total;
    }

    @Override
    public double calcularValorInventario() {
        double total = 0.0;
        for (ICatalogoComponent c : componentes) {
            total += c.calcularValorInventario();
        }
        return total;
    }

    @Override
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.US,
            "{\"tipo\":\"categoria\",\"nombre\":\"%s\",\"totalItems\":%d,\"stockTotal\":%d,\"valorInventario\":%.2f,\"hijos\":[",
            nombre.replace("\\", "\\\\").replace("\"", "\\\""),
            contarItems(),
            contarStockTotal(),
            calcularValorInventario()
        ));
        for (int i = 0; i < componentes.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(componentes.get(i).toJson());
        }
        sb.append("]}");
        return sb.toString();
    }
}
