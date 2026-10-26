package cinemauq.modelo;
/** Composite (hoja). */
public record Producto(String id, String nombre, long precio) implements ItemMenu {
    public Producto { if (nombre == null || nombre.isBlank() || precio <= 0) throw new cinemauq.excepcion.ValidacionException("Producto: nombre y precio > 0"); }
    @Override public String describir(String s) { return s + "- " + nombre + " ($" + precio + ")"; }
}
