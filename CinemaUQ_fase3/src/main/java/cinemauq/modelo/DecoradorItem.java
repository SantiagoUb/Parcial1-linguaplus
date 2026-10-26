package cinemauq.modelo;
import cinemauq.excepcion.ValidacionException;
/** PATRÓN DECORATOR (base): agrega complementos a un producto sin una subclase por combinación. */
public abstract class DecoradorItem implements ItemMenu {
    protected final ItemMenu base;
    protected DecoradorItem(ItemMenu base) {
        if (base == null) throw new ValidacionException("Producto base obligatorio");
        if (base instanceof Combo) throw new ValidacionException("Los combos tienen composición y precio cerrados: no admiten complementos");
        this.base = base;
    }
    @Override public String nombreBase() { return base.nombreBase(); }
    @Override public String describir(String s) { return s + "- " + nombre() + " ($" + precio() + ")"; }
}
