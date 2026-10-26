package cinemauq.reporte;
/** PATRÓN FACTORY (Simple Factory estática): el controlador pide un TipoReporte y no conoce las clases concretas. */
public final class FabricaReportes {
    private FabricaReportes() {}
    public static ReporteCine crear(TipoReporte tipo) {
        return switch (tipo) {
            case VENTAS_PERIODO -> new ReporteVentas(); case ENTRADAS_PELICULA -> new ReporteEntradas(); case OCUPACION -> new ReporteOcupacion();
            case PRODUCTOS_COMBOS -> new ReporteProductos(); case RECARGAS -> new ReporteRecargas(); case CANCELACIONES -> new ReporteCancelaciones();
            case PUNTOS -> new ReportePuntos();
        };
    }
}
