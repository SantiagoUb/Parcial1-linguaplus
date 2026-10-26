package cinemauq.controlador;
import cinemauq.modelo.*;
import java.util.*;
/** Selección en curso del cliente. Es solo un contenedor: las reglas las aplica Compra.Builder y ServicioCompra. */
public class Carrito {
    public final Cliente cliente; public final Map<Funcion, Set<Asiento>> asientos = new LinkedHashMap<>();
    public final List<LineaMenu> lineas = new ArrayList<>(); public Promocion promocion; public boolean conPuntos;
    public Carrito(Cliente c) { cliente = c; }
    public Compra construir() {
        var b = Compra.builder(cliente);
        asientos.forEach((f, s) -> s.forEach(a -> b.entrada(f, a)));
        lineas.forEach(l -> b.item(l.item(), l.cantidad()));
        if (conPuntos) b.pagarConPuntos(); else if (promocion != null) b.promocion(promocion);
        return b.build();
    }
    public boolean vacio() { return asientos.isEmpty() && lineas.isEmpty(); }
    public void limpiar() { asientos.clear(); lineas.clear(); promocion = null; conPuntos = false; }
}
