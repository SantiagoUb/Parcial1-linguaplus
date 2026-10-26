package cinemauq.vista;
import cinemauq.excepcion.CinemaException;
import cinemauq.reporte.Reporte;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
/** Utilidades de vista: alertas, tablas, gráficos. No contienen reglas de negocio. */
public final class Ui {
    private Ui() {}
    public static void error(String m) { new Alert(Alert.AlertType.ERROR, m).showAndWait(); }
    public static void info(String m) { new Alert(Alert.AlertType.INFORMATION, m).showAndWait(); }
    public static boolean confirmar(String m) { return new Alert(Alert.AlertType.CONFIRMATION, m).showAndWait().filter(b -> b == ButtonType.OK).isPresent(); }
    /** Ejecuta una acción del usuario mostrando los errores de negocio como mensajes. */
    public static void accion(Runnable r) {
        try { r.run(); }
        catch (CinemaException e) { error(e.getMessage()); }
        catch (NumberFormatException e) { error("Ingrese un valor numérico válido"); }
        catch (DateTimeParseException e) { error("Fecha u hora inválida (use HH:mm)"); }
        catch (RuntimeException e) { e.printStackTrace(); error("Error inesperado: " + e); }
    }
    public static String cop(long v) { return Reporte.cop(v); }
    public static TextField campo(String prompt) { var t = new TextField(); t.setPromptText(prompt); return t; }
    public static Label titulo(String t) { var l = new Label(t); l.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;"); return l; }
    public static HBox fila(Node... n) { var h = new HBox(8, n); h.setPadding(new Insets(4)); return h; }
    public static VBox columna(Node... n) { var v = new VBox(10, n); v.setPadding(new Insets(12)); return v; }
    public static <T> TableView<T> col(TableView<T> t, String titulo, Function<T, String> f) {
        TableColumn<T, String> c = new TableColumn<>(titulo); c.setCellValueFactory(x -> new SimpleStringProperty(f.apply(x.getValue()))); c.setPrefWidth(140); t.getColumns().add(c); return t;
    }
    public static <T> StringConverter<T> conv(Function<T, String> f) {
        return new StringConverter<T>() { public String toString(T o) { return o == null ? "" : f.apply(o); } public T fromString(String s) { return null; } };
    }
    public static TabPane pestanas(List<Pantalla> pantallas) {
        TabPane tp = new TabPane(); Map<Tab, Pantalla> m = new HashMap<>();
        for (Pantalla p : pantallas) { Tab t = new Tab(p.titulo(), p.vista()); t.setClosable(false); tp.getTabs().add(t); m.put(t, p); }
        tp.getSelectionModel().selectedItemProperty().addListener((o, a, nuevo) -> { if (nuevo != null) m.get(nuevo).refrescar(); });
        pantallas.get(0).refrescar(); return tp;
    }
    public static Node grafico(Reporte r) {
        if (r.serie.isEmpty()) return new Label("Sin datos para graficar");
        if (r.grafico == cinemauq.reporte.TipoGrafico.PASTEL) {
            PieChart p = new PieChart(); r.serie.forEach((k, v) -> p.getData().add(new PieChart.Data(k, v))); p.setTitle(r.titulo); return p;
        }
        CategoryAxis x = new CategoryAxis(); NumberAxis y = new NumberAxis(); XYChart<String, Number> ch;
        if (r.grafico == cinemauq.reporte.TipoGrafico.LINEAS) ch = new LineChart<>(x, y); else ch = new BarChart<>(x, y);
        XYChart.Series<String, Number> s = new XYChart.Series<>(); r.serie.forEach((k, v) -> s.getData().add(new XYChart.Data<String, Number>(k, v)));
        ch.getData().add(s); ch.setLegendVisible(false); ch.setAnimated(false); ch.setTitle(r.titulo); return ch;
    }
    public static PieChart pastel(String titulo, Map<String, Long> m) {
        PieChart p = new PieChart(); m.forEach((k, v) -> p.getData().add(new PieChart.Data(k, v))); p.setTitle(titulo); p.setPrefHeight(280); return p;
    }
    public static BarChart<String, Number> barras(String titulo, Map<String, Long> m) {
        BarChart<String, Number> b = new BarChart<>(new CategoryAxis(), new NumberAxis()); XYChart.Series<String, Number> s = new XYChart.Series<>();
        m.forEach((k, v) -> s.getData().add(new XYChart.Data<String, Number>(k, v))); b.getData().add(s); b.setLegendVisible(false); b.setAnimated(false); b.setTitle(titulo); b.setPrefHeight(280); return b;
    }
}
