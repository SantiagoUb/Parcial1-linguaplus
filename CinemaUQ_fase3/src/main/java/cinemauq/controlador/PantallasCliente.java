package cinemauq.controlador;
import cinemauq.modelo.*;
import cinemauq.vista.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.*;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/** RF-003 y RF-004. */
class CarteleraPantalla implements Pantalla {
    private final Carrito carrito; private final ListView<Pelicula> lista = new ListView<>(); private final TableView<Funcion> funciones = new TableView<>();
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("EEE dd/MM/yyyy"), HORA = DateTimeFormatter.ofPattern("HH:mm");
    CarteleraPantalla(Carrito c) { carrito = c; }
    public String titulo() { return "Cartelera"; }
    public Parent vista() {
        lista.setCellFactory(lv -> new ListCell<Pelicula>() { @Override protected void updateItem(Pelicula p, boolean vacio) {
            super.updateItem(p, vacio); setText(vacio || p == null ? null : p.titulo() + "  ·  " + p.genero() + "  ·  " + p.duracionMin() + " min  ·  " + p.clasificacion()); } });
        Ui.col(funciones, "Fecha", f -> f.inicio().format(FECHA)); Ui.col(funciones, "Hora", f -> f.inicio().format(HORA)); Ui.col(funciones, "Sala", f -> f.sala().nombre());
        Ui.col(funciones, "Precio", f -> Ui.cop(f.precioEntrada())); Ui.col(funciones, "Libres", f -> String.valueOf(f.sala().capacidad() - f.ocupados()));
        lista.getSelectionModel().selectedItemProperty().addListener((o, a, p) -> cargarFunciones(p));
        Button elegir = new Button("Elegir asientos…"); elegir.setOnAction(e -> {
            Funcion f = funciones.getSelectionModel().getSelectedItem();
            if (f == null) Ui.error("Seleccione una función"); else { SeleccionAsientos.mostrar(f, carrito); refrescar(); }
        });
        Label ayuda = new Label("Elija película → función → asientos. Luego vaya a la pestaña «Mi compra».");
        VBox der = Ui.columna(Ui.titulo("Funciones"), funciones, elegir, ayuda); VBox izq = Ui.columna(Ui.titulo("Películas"), lista);
        SplitPane sp = new SplitPane(izq, der); sp.setDividerPositions(0.4); return sp;
    }
    private void cargarFunciones(Pelicula p) {
        if (p == null) { funciones.getItems().clear(); return; }
        var ahora = ConfiguracionCine.getInstance().ahora();
        funciones.getItems().setAll(Sesion.ctx.datos.funciones.stream().filter(f -> f.pelicula().id().equals(p.id()) && !f.cancelada() && f.inicio().isAfter(ahora))
            .sorted(Comparator.comparing(Funcion::inicio)).collect(Collectors.toList()));
    }
    public void refrescar() { var sel = lista.getSelectionModel().getSelectedItem(); lista.getItems().setAll(Sesion.ctx.datos.peliculas); if (sel != null) lista.getSelectionModel().select(sel); }
}

/** RF-006..009 y RF-016: entradas, confitería, promoción, puntos y pago. */
class CompraPantalla implements Pantalla {
    private final Carrito carrito; private final Label entradas = new Label(), resumen = new Label();
    private final TableView<LineaMenu> items = new TableView<>(); private final ComboBox<ItemMenu> menu = new ComboBox<>();
    private final Spinner<Integer> cantidad = new Spinner<>(1, 20, 1); private final CheckBox grande = new CheckBox("Tamaño grande (+25 %)"), puntos = new CheckBox("Pagar con puntos");
    private final ComboBox<String> extra = new ComboBox<>(FXCollections.observableArrayList("Sin extra", "Queso extra (+$2.500)", "Caramelo (+$2.000)"));
    private final ComboBox<Promocion> promo = new ComboBox<>(); private final Button pagar = new Button("Pagar con tarjeta virtual");
    CompraPantalla(Carrito c) { carrito = c; }
    public String titulo() { return "Mi compra"; }
    public Parent vista() {
        Ui.col(items, "Elemento", l -> l.item().nombre()); Ui.col(items, "Cant.", l -> String.valueOf(l.cantidad())); Ui.col(items, "Subtotal", l -> Ui.cop(l.subtotal()));
        menu.setConverter(Ui.<ItemMenu>conv(i -> i.nombre() + " (" + Ui.cop(i.precio()) + (i instanceof Combo ? ", combo" : "") + ")")); menu.setPromptText("Producto o combo");
        extra.getSelectionModel().selectFirst(); promo.setPromptText("Sin promoción"); promo.setConverter(Ui.<Promocion>conv(p -> p.nombre()));
        Button agregar = new Button("Agregar confitería"), quitar = new Button("Quitar seleccionado"), vaciar = new Button("Vaciar compra"), sinPromo = new Button("Sin promoción");
        agregar.setOnAction(e -> Ui.accion(() -> {
            ItemMenu it = menu.getValue(); if (it == null) { Ui.error("Seleccione un producto o combo"); return; }
            if (grande.isSelected()) it = new TamanoGrande(it);
            int x = extra.getSelectionModel().getSelectedIndex();
            if (x == 1) it = new ConExtra(it, "queso extra", 2500); else if (x == 2) it = new ConExtra(it, "caramelo", 2000);
            carrito.lineas.add(new LineaMenu(it, cantidad.getValue())); refrescar();
        }));
        quitar.setOnAction(e -> { var l = items.getSelectionModel().getSelectedItem(); if (l != null) { carrito.lineas.remove(l); refrescar(); } });
        vaciar.setOnAction(e -> { carrito.limpiar(); puntos.setSelected(false); promo.setValue(null); refrescar(); });
        sinPromo.setOnAction(e -> promo.setValue(null));
        promo.valueProperty().addListener((o, a, b) -> { carrito.promocion = b; resumir(); });
        puntos.selectedProperty().addListener((o, a, b) -> { carrito.conPuntos = b; resumir(); });
        pagar.setOnAction(e -> Ui.accion(() -> {
            Compra c = carrito.construir(); Sesion.ctx.compra.pagar(c, carrito.cliente);
            Ui.info("¡Compra pagada!\nCódigo: " + c.id() + "\n" + (c.conPuntos() ? c.costoPuntos() + " puntos redimidos" : "Total: " + Ui.cop(c.total())));
            carrito.limpiar(); puntos.setSelected(false); promo.setValue(null); refrescar();
        }));
        return Ui.columna(Ui.titulo("Entradas"), entradas, Ui.titulo("Confitería"), Ui.fila(menu, cantidad, grande, extra, agregar), items, Ui.fila(quitar, vaciar),
            Ui.titulo("Promoción y pago"), Ui.fila(promo, sinPromo, puntos), resumen, pagar);
    }
    public void refrescar() {
        var ctx = Sesion.ctx.datos; List<ItemMenu> todos = new ArrayList<>(ctx.productos); todos.addAll(ctx.combos); menu.getItems().setAll(todos);
        var hoy = ConfiguracionCine.getInstance().ahora().toLocalDate(); var vigentes = ctx.promociones.stream().filter(p -> p.vigente(hoy)).collect(Collectors.toList());
        var actual = promo.getValue(); promo.getItems().setAll(vigentes); promo.setValue(actual != null && vigentes.contains(actual) ? actual : null);
        items.getItems().setAll(carrito.lineas); resumir();
    }
    private void resumir() {
        entradas.setText(carrito.asientos.isEmpty() ? "Sin entradas (elija asientos en la Cartelera)" : carrito.asientos.entrySet().stream()
            .map(en -> en.getKey().pelicula().titulo() + " · " + en.getKey().inicio().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")) + " · " + en.getKey().sala().nombre() + " · asientos "
                + en.getValue().stream().map(Asiento::codigo).collect(Collectors.joining(", "))).collect(Collectors.joining("\n")));
        carrito.promocion = promo.getValue(); carrito.conPuntos = puntos.isSelected();
        var hoy = ConfiguracionCine.getInstance().ahora().toLocalDate();
        try {
            Compra c = carrito.construir();
            resumen.setText("Subtotal: " + Ui.cop(c.subtotal()) + "    Descuento: " + Ui.cop(c.descuento()) + "    TOTAL: " + Ui.cop(c.total())
                + (c.conPuntos() ? "\nCosto en puntos: " + c.costoPuntos() + " (disponibles: " + carrito.cliente.puntos().disponibles(hoy) + ")" : "\nSaldo de la tarjeta: " + Ui.cop(carrito.cliente.tarjeta().saldo())));
            pagar.setDisable(false);
        } catch (RuntimeException ex) { resumen.setText(carrito.vacio() ? "La compra está vacía" : ex.getMessage()); pagar.setDisable(true); }
    }
}

/** RF-010 y RF-011. */
class TarjetaPantalla implements Pantalla {
    private final Label saldo = new Label(), estado = new Label(); private final TableView<Movimiento> tabla = new TableView<>();
    public String titulo() { return "Tarjeta virtual"; }
    public Parent vista() {
        saldo.setStyle("-fx-font-size: 22px;");
        Ui.col(tabla, "Fecha", m -> m.fecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))); Ui.col(tabla, "Tipo", m -> m.tipo().name());
        Ui.col(tabla, "Monto", m -> Ui.cop(m.monto())); Ui.col(tabla, "Saldo posterior", m -> Ui.cop(m.saldoPosterior())); Ui.col(tabla, "Referencia", Movimiento::referencia);
        return Ui.columna(Ui.titulo("Mi tarjeta"), saldo, estado, Ui.titulo("Movimientos"), tabla);
    }
    public void refrescar() {
        var t = ((Cliente) Sesion.usuario).tarjeta(); saldo.setText("Saldo: " + Ui.cop(t.saldo())); estado.setText("Estado: " + (t.activa() ? "ACTIVA" : "INACTIVA") + "   ·   N.º " + t.id());
        var l = new ArrayList<>(t.movimientos()); Collections.reverse(l); tabla.getItems().setAll(l);
    }
}

/** RF-014: lotes con su vigencia; se muestran en el orden en que se consumirán (FEFO). */
class PuntosPantalla implements Pantalla {
    private final Label total = new Label(); private final TableView<LotePuntos> tabla = new TableView<>();
    public String titulo() { return "Puntos"; }
    public Parent vista() {
        total.setStyle("-fx-font-size: 22px;");
        Ui.col(tabla, "Obtenido", l -> l.obtenido().toString()); Ui.col(tabla, "Vence", l -> l.vence().toString()); Ui.col(tabla, "Iniciales", l -> String.valueOf(l.inicial()));
        Ui.col(tabla, "Restantes", l -> String.valueOf(l.disponible())); Ui.col(tabla, "Estado", l -> l.vence().isAfter(LocalDate.now()) ? "Vigente" : "Vencido");
        Label regla = new Label("Se acumula 1 punto por cada $1.000 pagados en dinero. Al redimir, 1 punto equivale a $20. Se consumen primero los puntos que vencen antes.");
        return Ui.columna(Ui.titulo("Puntos CinemaUQ"), total, regla, tabla);
    }
    public void refrescar() {
        var c = ((Cliente) Sesion.usuario).puntos(); total.setText("Puntos válidos: " + c.disponibles(ConfiguracionCine.getInstance().ahora().toLocalDate()));
        tabla.getItems().setAll(c.lotes().stream().sorted(Comparator.comparing(LotePuntos::vence)).collect(Collectors.toList()));
    }
}

/** RF-012 y RF-013. */
class HistorialPantalla implements Pantalla {
    private final TableView<Compra> tabla = new TableView<>();
    public String titulo() { return "Mis compras"; }
    public Parent vista() {
        Ui.col(tabla, "Código", Compra::id); Ui.col(tabla, "Fecha", c -> c.creada().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        Ui.col(tabla, "Detalle", c -> c.entradas().size() + " entrada(s), " + c.lineas().size() + " ítem(s)" + (c.entradas().isEmpty() ? "" : " · " + c.entradas().get(0).funcion().pelicula().titulo()));
        Ui.col(tabla, "Total", c -> c.conPuntos() ? c.costoPuntos() + " pts" : Ui.cop(c.total())); Ui.col(tabla, "Estado", c -> c.estado().nombre());
        Ui.col(tabla, "Reembolsado", c -> c.montoReembolsado() > 0 ? Ui.cop(c.montoReembolsado()) : "—");
        Button cancelar = new Button("Solicitar cancelación");
        cancelar.setOnAction(e -> {
            Compra c = tabla.getSelectionModel().getSelectedItem(); if (c == null) { Ui.error("Seleccione una compra"); return; }
            if (!Ui.confirmar("Reembolso: más de 24 h antes → 100 %; entre 2 y 24 h → 80 %; menos de 2 h no es posible.\n¿Cancelar la compra " + c.id() + "?")) return;
            Ui.accion(() -> { Sesion.ctx.cancelacion.cancelarCompra(c, Sesion.usuario); Ui.info("Compra cancelada. Reembolsado: " + (c.conPuntos() ? "puntos restaurados" : Ui.cop(c.montoReembolsado()))); refrescar(); });
        });
        return Ui.columna(Ui.titulo("Historial de compras"), tabla, cancelar);
    }
    public void refrescar() {
        tabla.getItems().setAll(Sesion.ctx.datos.compras.todas().stream().filter(c -> c.cliente() == Sesion.usuario).sorted(Comparator.comparing(Compra::creada).reversed()).collect(Collectors.toList()));
    }
}
