package cinemauq.reporte;
import cinemauq.modelo.*;
import cinemauq.persistencia.DatosCine;
import java.util.*;
import java.util.stream.*;
public class ServicioEstadisticas {
    private static Map<String, Long> top(Map<String, Long> m, int n) {
        return m.entrySet().stream().sorted((a, b) -> Long.compare(b.getValue(), a.getValue())).limit(n).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }
    public Estadisticas calcular(Usuario admin, DatosCine d) {
        Autorizacion.exigirAdmin(admin);
        var todas = d.compras.todas(); long ventas = 0; int pag = 0; long reemb = 0; int nr = 0;
        Map<String, Long> pel = new HashMap<>(), prod = new HashMap<>();
        for (var c : todas) {
            if (c.estado() == Estados.PAGADA) {
                if (!c.conPuntos()) { ventas += c.total(); pag++; }
                c.entradas().forEach(e -> pel.merge(e.funcion().pelicula().titulo(), 1L, Long::sum));
                c.lineas().forEach(l -> prod.merge(l.item().nombreBase(), (long) l.cantidad(), Long::sum));
            } else if (c.estado() == Estados.REEMBOLSADA) { reemb += c.montoReembolsado(); nr++; }
        }
        double oc = d.funciones.stream().filter(f -> !f.cancelada()).mapToDouble(f -> 100.0 * f.ocupados() / f.sala().capacidad()).average().orElse(0);
        return new Estadisticas(ventas, pag, oc, top(pel, 5), top(prod, 5), reemb, nr);
    }
}
