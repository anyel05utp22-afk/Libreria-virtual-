package patrones.estructurales.composite;

import java.util.ArrayList;
import java.util.List;

public class CategoriaComposite implements ICatalogoComponent {
    private String nombre;
    private List<ICatalogoComponent> componentes = new ArrayList<>();

    public CategoriaComposite(String nombre) {
        this.nombre = nombre;
    }

    public void agregar(ICatalogoComponent comp) {
        componentes.add(comp);
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
        System.out.println(prefijo + "+ [Categoría] " + nombre + " (" + contarItems() + " elementos)");
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
}

