package cinemauq.modelo;
import java.time.LocalDate;
public class LotePuntos implements java.io.Serializable {
    private final String id = Ids.nuevo("LOT"), compraId; private final int inicial; private int restante;
    private final LocalDate obtenido, vence;
    public LotePuntos(String compraId, int cantidad, LocalDate obtenido) {
        this.compraId = compraId; this.inicial = cantidad; this.restante = cantidad; this.obtenido = obtenido;
        this.vence = obtenido.plusYears(ConfiguracionCine.VIGENCIA_PUNTOS_ANIOS);
    }
    boolean vigente(LocalDate hoy) { return hoy.isBefore(vence); }
    int restante() { return restante; } void consumir(int n) { restante -= n; } void devolver(int n) { restante += n; } void anular() { restante = 0; }
    public String id() { return id; } public String compraId() { return compraId; } public int inicial() { return inicial; }
    public int disponible() { return restante; } public LocalDate obtenido() { return obtenido; } public LocalDate vence() { return vence; }
    @Override public String toString() { return id + " [" + restante + "/" + inicial + "] obtenido " + obtenido + " vence " + vence; }
}
