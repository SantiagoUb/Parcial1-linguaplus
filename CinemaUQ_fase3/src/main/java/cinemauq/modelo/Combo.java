package cinemauq.modelo;
import cinemauq.excepcion.ValidacionException;
import java.util.*;
/** Composite (compuesto). El precio se calcula UNA sola vez: suma de componentes menos descuento (sin doble conteo). */
public class Combo implements ItemMenu {
    private final String id, nombre; private final int descuento; private final List<ItemMenu> componentes = new ArrayList<>();
    public Combo(String nombre, int descuentoPct) {
        if (descuentoPct < 0 || descuentoPct > 100) throw new ValidacionException("Descuento inválido");
        this.id = Ids.nuevo("CMB"); this.nombre = nombre; this.descuento = descuentoPct;
    }
    public Combo agregar(ItemMenu i) {
        if (i == this || (i instanceof Combo c && c.contiene(this))) throw new ValidacionException("Un combo no puede contenerse a sí mismo (ciclo)");
        componentes.add(i); return this;
    }
    public String id() { return id; }
    public List<ItemMenu> componentes() { return Collections.unmodifiableList(componentes); }
    public int descuento() { return descuento; }
    public boolean contiene(ItemMenu x) { return componentes.stream().anyMatch(c -> c == x || (c instanceof Combo cc && cc.contiene(x))); }
    public long precioLista() { return componentes.stream().mapToLong(ItemMenu::precio).sum(); }
    @Override public long precio() { return precioLista() * (100 - descuento) / 100; }
    @Override public String nombre() { return nombre; }
    @Override public String describir(String s) {
        StringBuilder sb = new StringBuilder(s + "+ " + nombre + " ($" + precio() + ")");
        for (ItemMenu i : componentes) sb.append("\n").append(i.describir(s + "   "));
        return sb.toString();
    }
}
