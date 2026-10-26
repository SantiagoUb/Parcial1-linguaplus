package cinemauq.reporte;
import java.util.Map;
public record Estadisticas(long ventasTotal, int comprasPagadas, double ocupacionPromedio, Map<String, Long> peliculasTop, Map<String, Long> productosTop, long reembolsosTotal, int reembolsos) {}
