package cinemauq.reporte;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
/** Resultado de una consulta de negocio. Es independiente del formato de salida (TXT/PDF/gráficos). */
public class Reporte {
    public final String titulo, periodo; public final LocalDateTime generado = LocalDateTime.now();
    public final List<String> columnas; public final List<List<String>> filas = new ArrayList<>();
    public final List<String> totales = new ArrayList<>();
    /** Datos para BarChart / LineChart / PieChart de JavaFX. */
    public final Map<String, Double> serie = new LinkedHashMap<>(); public TipoGrafico grafico = TipoGrafico.BARRAS;
    public Reporte(String titulo, String periodo, String... columnas) { this.titulo = titulo; this.periodo = periodo; this.columnas = List.of(columnas); }
    public void fila(Object... v) { filas.add(Arrays.stream(v).map(String::valueOf).toList()); }
    public static String cop(long v) { return String.format("$%,d", v); }

    private static String celda(String s, int w) { return s.length() > w ? s.substring(0, w - 1) + "~" : String.format("%-" + w + "s", s); }
    /** Representación en texto plano con columnas alineadas (la usan TXT y PDF). */
    public List<String> comoTexto() {
        List<String> l = new ArrayList<>(); var f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        l.add("CinemaUQ - " + titulo.toUpperCase()); l.add("Fecha de generación: " + generado.format(f));
        l.add("Período consultado: " + periodo); l.add("Registros encontrados: " + filas.size()); l.add("");
        int n = columnas.size(); int[] w = new int[n];
        for (int i = 0; i < n; i++) { w[i] = Math.min(columnas.get(i).length(), 18); for (var r : filas) w[i] = Math.min(18, Math.max(w[i], r.get(i).length())); }
        StringBuilder h = new StringBuilder(), sep = new StringBuilder();
        for (int i = 0; i < n; i++) { h.append(celda(columnas.get(i), w[i])).append(i < n - 1 ? " | " : ""); sep.append("-".repeat(w[i])).append(i < n - 1 ? "-+-" : ""); }
        l.add(h.toString()); l.add(sep.toString());
        for (var r : filas) { StringBuilder s = new StringBuilder(); for (int i = 0; i < n; i++) s.append(celda(r.get(i), w[i])).append(i < n - 1 ? " | " : ""); l.add(s.toString()); }
        l.add(sep.toString()); l.add("TOTALES"); totales.forEach(t -> l.add("  " + t));
        return l;
    }
}
