package cinemauq.servicio;
import cinemauq.modelo.Compra;
import java.time.LocalDateTime;
/** PATRÓN STRATEGY: cada política decide si aplica y qué porcentaje devuelve. */
public interface PoliticaReembolso {
    boolean aplica(Compra c);
    int porcentaje(Compra c, LocalDateTime ahora);
}
