package cinemauq.modelo;
import java.time.LocalDate;
/** Decisión: una sola promoción por compra, solo en pagos con dinero, porcentaje sobre el subtotal. */
public record Promocion(String codigo, String nombre, int porcentaje, LocalDate desde, LocalDate hasta) implements java.io.Serializable {
    public boolean vigente(LocalDate hoy) { return !hoy.isBefore(desde) && !hoy.isAfter(hasta); }
}
