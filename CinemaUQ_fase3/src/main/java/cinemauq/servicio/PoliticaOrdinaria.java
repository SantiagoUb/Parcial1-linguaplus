package cinemauq.servicio;
import cinemauq.excepcion.CancelacionNoPermitidaException;
import cinemauq.modelo.Compra;
import java.time.Duration;
import java.time.LocalDateTime;
/** >24 h: 100 %; entre 2 y 24 h: 80 %; <2 h: no permitido. */
public class PoliticaOrdinaria implements PoliticaReembolso {
    public boolean aplica(Compra c) { return true; }
    public int porcentaje(Compra c, LocalDateTime ahora) {
        LocalDateTime inicio = c.inicioMasTemprano()
                .orElseThrow(() -> new CancelacionNoPermitidaException("Las compras solo de confitería no son cancelables"));
        long min = Duration.between(ahora, inicio).toMinutes();
        if (min > 24 * 60) return 100;
        if (min >= 2 * 60) return 80;
        throw new CancelacionNoPermitidaException("No se puede cancelar con menos de 2 horas de anticipación");
    }
}
