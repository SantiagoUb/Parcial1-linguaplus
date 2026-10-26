package cinemauq.reporte;
public enum TipoReporte {
    VENTAS_PERIODO("Ventas por período"), ENTRADAS_PELICULA("Entradas por película y función"), OCUPACION("Ocupación de salas"),
    PRODUCTOS_COMBOS("Productos y combos"), RECARGAS("Recargas"), CANCELACIONES("Cancelaciones y reembolsos"), PUNTOS("Puntos");
    private final String etiqueta;
    TipoReporte(String e) { etiqueta = e; }
    @Override public String toString() { return etiqueta; }
}
