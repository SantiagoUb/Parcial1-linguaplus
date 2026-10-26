package cinemauq.servicio;
import cinemauq.modelo.Compra;
import java.time.LocalDateTime;
/** Función cancelada por el cine: 100 % siempre. */
public class PoliticaFuncionCancelada implements PoliticaReembolso {
    public boolean aplica(Compra c) { return c.hayFuncionCancelada(); }
    public int porcentaje(Compra c, LocalDateTime ahora) { return 100; }
}
