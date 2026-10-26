package cinemauq.controlador;
import cinemauq.excepcion.ValidacionException;
import cinemauq.modelo.*;
import cinemauq.reporte.*;
import cinemauq.vista.*;
import java.io.File;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

/** Todas las pantallas llaman a servicios que vuelven a validar el rol de administrador. */
abstract class BaseAdmin implements Pantalla {
    protected final cinemauq.ContextoCine ctx() { return Sesion.ctx; }
    protected final Usuario yo() { return Sesion.usuario; }
    protected static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    protected static int entero(TextField t) { return Integer.parseInt(t.getText().trim()); }
    protected static long largo(TextField t) { return Long.parseLong(t.getText().trim()); }
}

/** RF-017 */
class PeliculasAdmin extends BaseAdmin {
    private final TableView<Pelicula> tabla = new TableView<>(); private final TextField titulo = Ui.campo("Título"), genero = Ui.campo("Género"), dur = Ui.campo("Duración (min)"), clas = Ui.campo("Clasificación");
    public String titulo() { return "Películas"; }
    public Parent vista() {
        Ui.col(tabla, "Título", Pelicula::titulo); Ui.col(tabla, "Género", Pelicula::genero); Ui.col(tabla, "Duración", p -> p.duracionMin() + " min"); Ui.col(tabla, "Clasificación", Pelicula::clasificacion);
        tabla.getSelectionModel().selectedItemProperty().addListener((o, a, p) -> { if (p != null) { titulo.setText(p.titulo()); genero.setText(p.genero()); dur.setText("" + p.duracionMin()); clas.setText(p.clasificacion()); } });
        Button crear = new Button("Crear"), act = new Button("Actualizar"), del = new Button("Eliminar");
        crear.setOnAction(e -> Ui.accion(() -> { ctx().catalogo.crearPelicula(yo(), titulo.getText(), genero.getText(), entero(dur), clas.getText()); refrescar(); }));
        act.setOnAction(e -> Ui.accion(() -> { var p = sel(); ctx().catalogo.actualizarPelicula(yo(), p.id(), titulo.getText(), genero.getText(), entero(dur), clas.getText()); refrescar(); }));
        del.setOnAction(e -> Ui.accion(() -> { var p = sel(); if (Ui.confirmar("¿Eliminar «" + p.titulo() + "»?")) { ctx().catalogo.eliminarPelicula(yo(), p.id()); refrescar(); } }));
        return Ui.columna(tabla, Ui.fila(titulo, genero, dur, clas), Ui.fila(crear, act, del));
    }
    private Pelicula sel() { var p = tabla.getSelectionModel().getSelectedItem(); if (p == null) throw new ValidacionException("Seleccione una película"); return p; }
    public void refrescar() { tabla.getItems().setAll(ctx().datos.peliculas); }
}

/** RF-018 y RF-019 (los asientos se generan por filas y columnas). */
class SalasAdmin extends BaseAdmin {
    private final TableView<Sala> tabla = new TableView<>(); private final TextField nombre = Ui.campo("Nombre"), filas = Ui.campo("Filas (1-26)"), cols = Ui.campo("Asientos por fila");
    public String titulo() { return "Salas y asientos"; }
    public Parent vista() {
        Ui.col(tabla, "Sala", Sala::nombre); Ui.col(tabla, "Filas", s -> "" + s.filas()); Ui.col(tabla, "Asientos por fila", s -> "" + s.columnas()); Ui.col(tabla, "Capacidad", s -> "" + s.capacidad());
        tabla.getSelectionModel().selectedItemProperty().addListener((o, a, s) -> { if (s != null) { nombre.setText(s.nombre()); filas.setText("" + s.filas()); cols.setText("" + s.columnas()); } });
        Button crear = new Button("Crear"), act = new Button("Actualizar"), del = new Button("Eliminar");
        crear.setOnAction(e -> Ui.accion(() -> { ctx().catalogo.crearSala(yo(), nombre.getText(), entero(filas), entero(cols)); refrescar(); }));
        act.setOnAction(e -> Ui.accion(() -> { ctx().catalogo.actualizarSala(yo(), sel().id(), nombre.getText(), entero(filas), entero(cols)); refrescar(); }));
        del.setOnAction(e -> Ui.accion(() -> { var s = sel(); if (Ui.confirmar("¿Eliminar " + s.nombre() + "?")) { ctx().catalogo.eliminarSala(yo(), s.id()); refrescar(); } }));
        return Ui.columna(tabla, Ui.fila(nombre, filas, cols), Ui.fila(crear, act, del));
    }
    private Sala sel() { var s = tabla.getSelectionModel().getSelectedItem(); if (s == null) throw new ValidacionException("Seleccione una sala"); return s; }
    public void refrescar() { tabla.getItems().setAll(ctx().datos.salas); }
}

/** RF-020 y RF-026 */
class FuncionesAdmin extends BaseAdmin {
    private final TableView<Funcion> tabla = new TableView<>(); private final ComboBox<Pelicula> pel = new ComboBox<>(); private final ComboBox<Sala> sala = new ComboBox<>();
    private final DatePicker fecha = new DatePicker(LocalDate.now().plusDays(1)); private final TextField hora = Ui.campo("Hora HH:mm"), precio = Ui.campo("Precio entrada");
    public String titulo() { return "Funciones"; }
    public Parent vista() {
        pel.setPromptText("Película"); pel.setConverter(Ui.<Pelicula>conv(Pelicula::titulo)); sala.setPromptText("Sala"); sala.setConverter(Ui.<Sala>conv(Sala::nombre));
        Ui.col(tabla, "Película", f -> f.pelicula().titulo()); Ui.col(tabla, "Sala", f -> f.sala().nombre()); Ui.col(tabla, "Inicio", f -> f.inicio().format(DT));
        Ui.col(tabla, "Precio", f -> Ui.cop(f.precioEntrada())); Ui.col(tabla, "Ocupación", f -> f.ocupados() + "/" + f.sala().capacidad()); Ui.col(tabla, "Estado", f -> f.cancelada() ? "CANCELADA" : "Programada");
        Button prog = new Button("Programar"), repro = new Button("Reprogramar seleccionada"), del = new Button("Eliminar"), canc = new Button("Cancelar función (reembolsa)");
        prog.setOnAction(e -> Ui.accion(() -> { ctx().catalogo.programarFuncion(yo(), req(pel.getValue(), "película").id(), req(sala.getValue(), "sala").id(), inicio(), largo(precio)); refrescar(); }));
        repro.setOnAction(e -> Ui.accion(() -> { ctx().catalogo.reprogramarFuncion(yo(), sel(), req(sala.getValue(), "sala").id(), inicio(), largo(precio)); refrescar(); }));
        del.setOnAction(e -> Ui.accion(() -> { ctx().catalogo.eliminarFuncion(yo(), sel()); refrescar(); }));
        canc.setOnAction(e -> Ui.accion(() -> { var f = sel();
            if (Ui.confirmar("Se cancelará la función y se reembolsará al 100 % TODA compra que la incluya. ¿Continuar?")) { int n = ctx().cancelacion.cancelarFuncion(yo(), f); Ui.info("Función cancelada. Compras reembolsadas: " + n); refrescar(); } }));
        return Ui.columna(tabla, Ui.fila(pel, sala, fecha, hora, precio), Ui.fila(prog, repro, del, canc));
    }
    private LocalDateTime inicio() { if (fecha.getValue() == null) throw new ValidacionException("Seleccione la fecha"); return fecha.getValue().atTime(LocalTime.parse(hora.getText().trim())); }
    private static <T> T req(T v, String n) { if (v == null) throw new ValidacionException("Seleccione " + n); return v; }
    private Funcion sel() { var f = tabla.getSelectionModel().getSelectedItem(); if (f == null) throw new ValidacionException("Seleccione una función"); return f; }
    public void refrescar() {
        tabla.getItems().setAll(ctx().datos.funciones.stream().sorted(Comparator.comparing(Funcion::inicio).reversed()).collect(Collectors.toList()));
        pel.getItems().setAll(ctx().datos.peliculas); sala.getItems().setAll(ctx().datos.salas);
    }
}

/** RF-021 y RF-022 */
class ConfiteriaAdmin extends BaseAdmin {
    private final TableView<Producto> prods = new TableView<>(); private final TableView<Combo> combos = new TableView<>();
    private final TextField nombre = Ui.campo("Producto"), precio = Ui.campo("Precio"), cNombre = Ui.campo("Combo"), cDesc = Ui.campo("Descuento %");
    private final ListView<Producto> elegibles = new ListView<>();
    public String titulo() { return "Productos y combos"; }
    public Parent vista() {
        Ui.col(prods, "Producto", Producto::nombre); Ui.col(prods, "Precio", p -> Ui.cop(p.precio()));
        prods.getSelectionModel().selectedItemProperty().addListener((o, a, p) -> { if (p != null) { nombre.setText(p.nombre()); precio.setText("" + p.precio()); } });
        elegibles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); elegibles.setPrefHeight(120);
        elegibles.setCellFactory(lv -> new ListCell<Producto>() { @Override protected void updateItem(Producto p, boolean v) { super.updateItem(p, v); setText(v || p == null ? null : p.nombre() + " (" + Ui.cop(p.precio()) + ")"); } });
        Ui.col(combos, "Combo", Combo::nombre); Ui.col(combos, "Precio lista", c -> Ui.cop(c.precioLista())); Ui.col(combos, "Descuento", c -> c.descuento() + " %"); Ui.col(combos, "Precio final", c -> Ui.cop(c.precio()));
        Ui.col(combos, "Composición", c -> c.componentes().stream().map(ItemMenu::nombre).collect(Collectors.joining(", ")));
        Button crear = new Button("Crear producto"), act = new Button("Actualizar"), del = new Button("Eliminar");
        crear.setOnAction(e -> Ui.accion(() -> { ctx().catalogo.crearProducto(yo(), nombre.getText(), largo(precio)); refrescar(); }));
        act.setOnAction(e -> Ui.accion(() -> { ctx().catalogo.actualizarProducto(yo(), req(prods.getSelectionModel().getSelectedItem(), "un producto"), nombre.getText(), largo(precio)); refrescar(); }));
        del.setOnAction(e -> Ui.accion(() -> { ctx().catalogo.eliminarProducto(yo(), req(prods.getSelectionModel().getSelectedItem(), "un producto")); refrescar(); }));
        Button cCrear = new Button("Crear combo con los productos marcados"), cDel = new Button("Eliminar combo");
        cCrear.setOnAction(e -> Ui.accion(() -> { ctx().catalogo.crearCombo(yo(), cNombre.getText(), entero(cDesc), new ArrayList<>(elegibles.getSelectionModel().getSelectedItems())); refrescar(); }));
        cDel.setOnAction(e -> Ui.accion(() -> { ctx().catalogo.eliminarCombo(yo(), req(combos.getSelectionModel().getSelectedItem(), "un combo")); refrescar(); }));
        VBox izq = Ui.columna(Ui.titulo("Productos"), prods, Ui.fila(nombre, precio), Ui.fila(crear, act, del));
        VBox der = Ui.columna(Ui.titulo("Combos"), combos, Ui.titulo("Componentes (Ctrl+clic para varios)"), elegibles, Ui.fila(cNombre, cDesc), Ui.fila(cCrear, cDel));
        return new SplitPane(izq, der);
    }
    private static <T> T req(T v, String n) { if (v == null) throw new ValidacionException("Seleccione " + n); return v; }
    public void refrescar() { prods.getItems().setAll(ctx().datos.productos); elegibles.getItems().setAll(ctx().datos.productos); combos.getItems().setAll(ctx().datos.combos); }
}

/** RF-023 */
class PromocionesAdmin extends BaseAdmin {
    private final TableView<Promocion> tabla = new TableView<>(); private final TextField cod = Ui.campo("Código"), nom = Ui.campo("Nombre"), pct = Ui.campo("% descuento");
    private final DatePicker desde = new DatePicker(LocalDate.now()), hasta = new DatePicker(LocalDate.now().plusMonths(1));
    public String titulo() { return "Promociones"; }
    public Parent vista() {
        Ui.col(tabla, "Código", Promocion::codigo); Ui.col(tabla, "Nombre", Promocion::nombre); Ui.col(tabla, "Descuento", p -> p.porcentaje() + " %");
        Ui.col(tabla, "Desde", p -> p.desde().toString()); Ui.col(tabla, "Hasta", p -> p.hasta().toString()); Ui.col(tabla, "Estado", p -> p.vigente(LocalDate.now()) ? "Vigente" : "No vigente");
        Button crear = new Button("Crear"), del = new Button("Eliminar");
        crear.setOnAction(e -> Ui.accion(() -> { ctx().catalogo.crearPromocion(yo(), cod.getText(), nom.getText(), entero(pct), desde.getValue(), hasta.getValue()); refrescar(); }));
        del.setOnAction(e -> Ui.accion(() -> { var p = tabla.getSelectionModel().getSelectedItem(); if (p == null) throw new ValidacionException("Seleccione una promoción"); ctx().catalogo.eliminarPromocion(yo(), p); refrescar(); }));
        Label regla = new Label("Regla: una promoción por compra, solo con pago en dinero, sobre el subtotal.");
        return Ui.columna(tabla, regla, Ui.fila(cod, nom, pct, desde, hasta), Ui.fila(crear, del));
    }
    public void refrescar() { tabla.getItems().setAll(ctx().datos.promociones); }
}

/** RF-024 */
class ClientesAdmin extends BaseAdmin {
    private final TableView<Cliente> tabla = new TableView<>();
    public String titulo() { return "Clientes"; }
    public Parent vista() {
        Ui.col(tabla, "Nombre", Cliente::nombre); Ui.col(tabla, "Correo", Cliente::email); Ui.col(tabla, "Tarjeta", c -> c.tarjeta().id()); Ui.col(tabla, "Estado", c -> c.tarjeta().activa() ? "Activa" : "Inactiva");
        Ui.col(tabla, "Saldo", c -> Ui.cop(c.tarjeta().saldo())); Ui.col(tabla, "Puntos válidos", c -> "" + c.puntos().disponibles(LocalDate.now()));
        return Ui.columna(Ui.titulo("Clientes registrados"), tabla);
    }
    public void refrescar() { tabla.getItems().setAll(ctx().datos.clientes); }
}

/** RF-025 */
class ComprasAdmin extends BaseAdmin {
    private final TableView<Compra> tabla = new TableView<>();
    public String titulo() { return "Compras"; }
    public Parent vista() {
        Ui.col(tabla, "Código", Compra::id); Ui.col(tabla, "Fecha", c -> c.creada().format(DT)); Ui.col(tabla, "Cliente", c -> c.cliente().nombre());
        Ui.col(tabla, "Entradas", c -> "" + c.entradas().size()); Ui.col(tabla, "Ítems", c -> "" + c.lineas().size());
        Ui.col(tabla, "Total", c -> c.conPuntos() ? c.costoPuntos() + " pts" : Ui.cop(c.total())); Ui.col(tabla, "Estado", c -> c.estado().nombre());
        return Ui.columna(Ui.titulo("Compras realizadas"), tabla);
    }
    public void refrescar() { tabla.getItems().setAll(ctx().datos.compras.todas().stream().sorted(Comparator.comparing(Compra::creada).reversed()).collect(Collectors.toList())); }
}

/** RF-027: solo administrador. */
class RecargasAdmin extends BaseAdmin {
    private final ComboBox<Cliente> cli = new ComboBox<>(); private final TextField monto = Ui.campo("Monto a recargar"); private final Label saldo = new Label();
    public String titulo() { return "Recargas"; }
    public Parent vista() {
        cli.setPromptText("Cliente"); cli.setConverter(Ui.<Cliente>conv(c -> c.nombre() + " · " + c.email()));
        cli.valueProperty().addListener((o, a, c) -> actualizarSaldo());
        Button rec = new Button("Recargar tarjeta");
        rec.setOnAction(e -> Ui.accion(() -> {
            var c = cli.getValue(); if (c == null) throw new ValidacionException("Seleccione un cliente");
            ctx().recargar(yo(), c, largo(monto)); Ui.info("Recarga registrada. Nuevo saldo: " + Ui.cop(c.tarjeta().saldo())); monto.clear(); actualizarSaldo();
        }));
        return Ui.columna(Ui.titulo("Recarga de tarjetas virtuales"), Ui.fila(cli, monto, rec), saldo);
    }
    private void actualizarSaldo() { var c = cli.getValue(); saldo.setText(c == null ? "" : "Saldo actual: " + Ui.cop(c.tarjeta().saldo())); }
    public void refrescar() { var s = cli.getValue(); cli.getItems().setAll(ctx().datos.clientes); cli.setValue(s); actualizarSaldo(); }
}

/** RF-028: reportes exportables (TXT/PDF) y visuales. */
class ReportesAdmin extends BaseAdmin {
    private final ComboBox<TipoReporte> tipo = new ComboBox<>(FXCollections.observableArrayList(TipoReporte.values()));
    private final DatePicker desde = new DatePicker(LocalDate.now().minusDays(90)), hasta = new DatePicker(LocalDate.now().plusDays(10));
    private final TableView<List<String>> tabla = new TableView<>(); private final Label totales = new Label(); private final StackPane grafico = new StackPane(); private Reporte actual;
    public String titulo() { return "Reportes"; }
    public Parent vista() {
        tipo.getSelectionModel().selectFirst(); tabla.setPrefHeight(250); grafico.setPrefHeight(300);
        Button gen = new Button("Generar"), txt = new Button("Exportar TXT"), pdf = new Button("Exportar PDF");
        gen.setOnAction(e -> Ui.accion(this::generar));
        txt.setOnAction(e -> exportar(new GeneradorTXT())); pdf.setOnAction(e -> exportar(new AdaptadorPDF()));
        return Ui.columna(Ui.fila(tipo, new Label("Desde"), desde, new Label("Hasta"), hasta, gen, txt, pdf), tabla, totales, grafico);
    }
    private void generar() {
        actual = ctx().reportes.generar(yo(), tipo.getValue(), ctx().datos, desde.getValue(), hasta.getValue());
        tabla.getColumns().clear();
        for (int i = 0; i < actual.columnas.size(); i++) {
            final int idx = i; TableColumn<List<String>, String> c = new TableColumn<>(actual.columnas.get(i)); c.setPrefWidth(130);
            c.setCellValueFactory(x -> new SimpleStringProperty(x.getValue().get(idx))); tabla.getColumns().add(c);
        }
        tabla.getItems().setAll(actual.filas); totales.setText(actual.filas.size() + " registro(s)   |   " + String.join("   |   ", actual.totales));
        grafico.getChildren().setAll(Ui.grafico(actual));
    }
    private void exportar(GeneradorReporte g) {
        if (actual == null) { Ui.error("Primero genere un reporte"); return; }
        FileChooser fc = new FileChooser(); fc.setInitialFileName(actual.titulo.replace(' ', '_') + "." + g.extension());
        File f = fc.showSaveDialog(App.ventana()); if (f == null) return;
        Ui.accion(() -> { try { ctx().reportes.exportar(yo(), actual, g, f.toPath()); Ui.info("Reporte exportado en:\n" + f.getAbsolutePath()); } catch (IOException ex) { Ui.error("No se pudo escribir el archivo: " + ex.getMessage()); } });
    }
    public void refrescar() {}
}

/** RF-029 */
class EstadisticasAdmin extends BaseAdmin {
    private final VBox contenido = new VBox(10);
    public String titulo() { return "Estadísticas"; }
    public Parent vista() { contenido.setPadding(new javafx.geometry.Insets(12)); return new ScrollPane(contenido); }
    public void refrescar() {
        Ui.accion(() -> {
            var e = ctx().estadisticas.calcular(yo(), ctx().datos);
            Label l = new Label("Ventas (dinero): " + Ui.cop(e.ventasTotal()) + "  ·  Compras pagadas: " + e.comprasPagadas() + "  ·  Ocupación promedio: " + String.format("%.1f %%", e.ocupacionPromedio())
                + "  ·  Reembolsos: " + e.reembolsos() + " (" + Ui.cop(e.reembolsosTotal()) + ")");
            l.setStyle("-fx-font-size: 14px;");
            contenido.getChildren().setAll(Ui.titulo("Estadísticas generales"), l, new HBox(10, Ui.barras("Películas con más entradas", e.peliculasTop()), Ui.pastel("Productos más vendidos", e.productosTop())));
        });
    }
}
