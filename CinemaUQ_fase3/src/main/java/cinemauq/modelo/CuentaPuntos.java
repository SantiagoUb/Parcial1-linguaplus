package cinemauq.modelo;
import cinemauq.excepcion.*;
import java.time.LocalDate;
import java.util.*;
/** Lotes independientes; redención FEFO (primero los que vencen antes). */
public class CuentaPuntos implements java.io.Serializable {
    public record Consumo(LotePuntos lote, int cantidad) implements java.io.Serializable {}
    private final List<LotePuntos> lotes = new ArrayList<>();
    public synchronized void acumular(int pts, String compraId, LocalDate hoy) {
        if (pts > 0) lotes.add(new LotePuntos(compraId, pts, hoy));
    }
    public synchronized int disponibles(LocalDate hoy) {
        return lotes.stream().filter(l -> l.vigente(hoy)).mapToInt(LotePuntos::restante).sum();
    }
    public synchronized List<Consumo> redimir(int pts, LocalDate hoy) {
        if (pts <= 0) throw new ValidacionException("Cantidad de puntos inválida");
        if (disponibles(hoy) < pts) throw new PuntosInsuficientesException("Puntos válidos insuficientes: " + disponibles(hoy) + " < " + pts);
        List<LotePuntos> orden = lotes.stream().filter(l -> l.vigente(hoy) && l.restante() > 0)
                .sorted(Comparator.comparing(LotePuntos::vence)).toList();
        List<Consumo> usados = new ArrayList<>(); int falta = pts;
        for (LotePuntos l : orden) {
            if (falta == 0) break;
            int t = Math.min(falta, l.restante()); l.consumir(t); usados.add(new Consumo(l, t)); falta -= t;
        }
        return usados;
    }
    /** Devuelve a su lote original (conservan vencimiento original) los puntos de una compra reembolsada. */
    public synchronized void restaurar(List<Consumo> consumos, int porcentaje) {
        for (Consumo c : consumos) c.lote().devolver(c.cantidad() * porcentaje / 100);
    }
    /** Anula lo que quede de los puntos ganados por una compra reembolsada. */
    public synchronized void revocarPorCompra(String compraId) {
        lotes.stream().filter(l -> l.compraId().equals(compraId)).forEach(LotePuntos::anular);
    }
    public synchronized List<LotePuntos> lotes() { return List.copyOf(lotes); }
}
