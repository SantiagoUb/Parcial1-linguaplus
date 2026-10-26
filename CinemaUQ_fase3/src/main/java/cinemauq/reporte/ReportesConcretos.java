package cinemauq.reporte;
import cinemauq.modelo.*;
import cinemauq.persistencia.DatosCine;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.*;
/** Los 7 reportes mínimos. Todos calculan sobre las operaciones reales almacenadas. */
final class Util {
    static final DateTimeFormatter F = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
    static boolean en(LocalDate f, LocalDate d, LocalDate h) { return !f.isBefore(d) && !f.isAfter(h); }
    static String per(LocalDate d, LocalDate h) { return d + " a " + h; }
    static List<Compra> compras(DatosCine d, LocalDate a, LocalDate b) { return d.compras.todas().stream().filter(c -> en(c.creada().toLocalDate(), a, b)).sorted(Comparator.comparing(Compra::creada)).toList(); }
}
class ReporteVentas implements ReporteCine {
    public Reporte generar(DatosCine d, LocalDate a, LocalDate b) {
        var r = new Reporte("Ventas por período", Util.per(a, b), "Compra", "Fecha", "Cliente", "Estado", "Total"); r.grafico = TipoGrafico.LINEAS;
        long vendido = 0, reemb = 0; int pagadas = 0;
        for (var c : Util.compras(d, a, b)) {
            r.fila(c.id(), c.creada().format(Util.F), c.cliente().nombre(), c.estado().nombre() + (c.conPuntos() ? " (pts)" : ""), c.conPuntos() ? c.costoPuntos() + " pts" : Reporte.cop(c.total()));
            if (c.estado() == Estados.PAGADA && !c.conPuntos()) { vendido += c.total(); pagadas++; r.serie.merge(c.creada().toLocalDate().toString(), (double) c.total(), Double::sum); }
            if (c.estado() == Estados.REEMBOLSADA) reemb += c.montoReembolsado();
        }
        r.totales.add("Compras pagadas en dinero: " + pagadas); r.totales.add("Valor vendido vigente: " + Reporte.cop(vendido)); r.totales.add("Reembolsado en el período: " + Reporte.cop(reemb));
        return r;
    }
}
class ReporteEntradas implements ReporteCine {
    public Reporte generar(DatosCine d, LocalDate a, LocalDate b) {
        var r = new Reporte("Entradas por película y función", Util.per(a, b), "Película", "Sala", "Función", "Entradas");
        Map<Funcion, Long> porF = new LinkedHashMap<>(); Map<String, Long> porP = new TreeMap<>();
        for (var c : Util.compras(d, a, b)) if (c.estado() == Estados.PAGADA) for (var e : c.entradas()) { porF.merge(e.funcion(), 1L, Long::sum); porP.merge(e.funcion().pelicula().titulo(), 1L, Long::sum); }
        porF.entrySet().stream().sorted(Comparator.comparing(e -> e.getKey().inicio())).forEach(e -> r.fila(e.getKey().pelicula().titulo(), e.getKey().sala().nombre(), e.getKey().inicio().format(Util.F), e.getValue()));
        porP.forEach((k, v) -> r.serie.put(k, v.doubleValue())); r.totales.add("Entradas vendidas vigentes: " + porP.values().stream().mapToLong(x -> x).sum());
        return r;
    }
}
class ReporteOcupacion implements ReporteCine {
    public Reporte generar(DatosCine d, LocalDate a, LocalDate b) {
        var r = new Reporte("Ocupación de salas", Util.per(a, b), "Película", "Sala", "Función", "Ocupados", "Capacidad", "%");
        long oc = 0, cap = 0;
        for (var f : d.funciones.stream().filter(f -> Util.en(f.inicio().toLocalDate(), a, b) && !f.cancelada()).sorted(Comparator.comparing(Funcion::inicio)).toList()) {
            double p = 100.0 * f.ocupados() / f.sala().capacidad(); oc += f.ocupados(); cap += f.sala().capacidad();
            r.fila(f.pelicula().titulo(), f.sala().nombre(), f.inicio().format(Util.F), f.ocupados(), f.sala().capacidad(), String.format("%.1f", p));
            r.serie.merge(f.sala().nombre(), (double) f.ocupados(), Double::sum);
        }
        r.grafico = TipoGrafico.PASTEL; r.totales.add("Asientos ocupados: " + oc + " de " + cap); r.totales.add(String.format("Ocupación global: %.1f %%", cap == 0 ? 0 : 100.0 * oc / cap));
        return r;
    }
}
class ReporteProductos implements ReporteCine {
    public Reporte generar(DatosCine d, LocalDate a, LocalDate b) {
        var r = new Reporte("Productos y combos", Util.per(a, b), "Elemento", "Tipo", "Cantidad", "Ingresos");
        Map<String, long[]> m = new HashMap<>(); Map<String, String> tipo = new HashMap<>();
        for (var c : Util.compras(d, a, b)) if (c.estado() == Estados.PAGADA) for (var l : c.lineas()) {
            var k = l.item().nombreBase(); tipo.put(k, l.item() instanceof Combo ? "Combo" : "Producto");
            var v = m.computeIfAbsent(k, x -> new long[2]); v[0] += l.cantidad(); v[1] += c.conPuntos() ? 0 : l.subtotal();
        }
        m.entrySet().stream().sorted((x, y) -> Long.compare(y.getValue()[0], x.getValue()[0])).forEach(e -> { r.fila(e.getKey(), tipo.get(e.getKey()), e.getValue()[0], Reporte.cop(e.getValue()[1])); r.serie.put(e.getKey(), (double) e.getValue()[0]); });
        r.grafico = TipoGrafico.PASTEL; r.totales.add("Unidades vendidas: " + m.values().stream().mapToLong(v -> v[0]).sum());
        return r;
    }
}
class ReporteRecargas implements ReporteCine {
    public Reporte generar(DatosCine d, LocalDate a, LocalDate b) {
        var r = new Reporte("Recargas", Util.per(a, b), "Fecha", "Cliente", "Tarjeta", "Monto"); r.grafico = TipoGrafico.LINEAS; long tot = 0;
        record Rec(LocalDateTime f, String cli, String tj, long m) {}
        List<Rec> l = new ArrayList<>();
        for (var c : d.clientes) for (var m : c.tarjeta().movimientos()) if (m.tipo() == TipoMovimiento.RECARGA && Util.en(m.fecha().toLocalDate(), a, b)) l.add(new Rec(m.fecha(), c.nombre(), c.tarjeta().id(), m.monto()));
        l.sort(Comparator.comparing(Rec::f));
        for (var x : l) { r.fila(x.f().format(Util.F), x.cli(), x.tj(), Reporte.cop(x.m())); tot += x.m(); r.serie.merge(x.f().toLocalDate().toString(), (double) x.m(), Double::sum); }
        r.totales.add("Total recargado: " + Reporte.cop(tot)); return r;
    }
}
class ReporteCancelaciones implements ReporteCine {
    public Reporte generar(DatosCine d, LocalDate a, LocalDate b) {
        var r = new Reporte("Cancelaciones y reembolsos", Util.per(a, b), "Compra", "Cliente", "Cancelada", "Motivo", "Pagado", "Devuelto"); long dev = 0; int n = 0;
        var l = d.compras.todas().stream().filter(c -> c.estado() == Estados.REEMBOLSADA && Util.en(c.reembolsadaEn().toLocalDate(), a, b)).sorted(Comparator.comparing(Compra::reembolsadaEn)).toList();
        for (var c : l) {
            boolean cine = c.hayFuncionCancelada(); n++; dev += c.montoReembolsado();
            r.fila(c.id(), c.cliente().nombre(), c.reembolsadaEn().format(Util.F), cine ? "Función cancelada" : "Solicitud cliente", c.conPuntos() ? c.costoPuntos() + " pts" : Reporte.cop(c.total()), Reporte.cop(c.montoReembolsado()));
            r.serie.merge(c.reembolsadaEn().toLocalDate().toString(), (double) c.montoReembolsado(), Double::sum);
        }
        r.totales.add("Compras canceladas: " + n); r.totales.add("Total devuelto: " + Reporte.cop(dev)); return r;
    }
}
class ReportePuntos implements ReporteCine {
    public Reporte generar(DatosCine d, LocalDate a, LocalDate b) {
        var r = new Reporte("Puntos CinemaUQ", Util.per(a, b), "Cliente", "Acumulados", "Redimidos", "Disponibles hoy"); long ta = 0, tr = 0; var hoy = LocalDate.now();
        for (var c : d.clientes) {
            long acum = c.puntos().lotes().stream().filter(l -> Util.en(l.obtenido(), a, b)).mapToLong(LotePuntos::inicial).sum();
            long red = d.compras.todas().stream().filter(x -> x.cliente() == c && x.conPuntos() && x.estado() == Estados.PAGADA && Util.en(x.creada().toLocalDate(), a, b)).mapToLong(Compra::costoPuntos).sum();
            r.fila(c.nombre(), acum, red, c.puntos().disponibles(hoy)); ta += acum; tr += red; r.serie.put(c.nombre(), (double) acum);
        }
        r.totales.add("Puntos acumulados: " + ta); r.totales.add("Puntos redimidos: " + tr); return r;
    }
}
