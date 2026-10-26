package cinemauq.controlador;
import cinemauq.modelo.*;
import cinemauq.vista.Ui;
import java.util.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
/** RF-005: mapa gráfico. La disponibilidad se consulta por función + asiento; el servicio la vuelve a validar al pagar. */
final class SeleccionAsientos {
    private SeleccionAsientos() {}
    static void mostrar(Funcion f, Carrito carrito) {
        Set<Asiento> elegidos = new LinkedHashSet<>(carrito.asientos.getOrDefault(f, Set.of()));
        GridPane g = new GridPane(); g.setHgap(6); g.setVgap(6); g.setAlignment(Pos.CENTER);
        for (Asiento a : f.sala().asientos()) {
            ToggleButton b = new ToggleButton(a.codigo()); b.setPrefWidth(52);
            if (f.estaOcupado(a)) { b.setDisable(true); b.setStyle("-fx-base: #d9534f;"); }
            else {
                b.setSelected(elegidos.contains(a)); if (b.isSelected()) b.setStyle("-fx-base: #5cb85c;");
                b.selectedProperty().addListener((o, antes, sel) -> { b.setStyle(sel ? "-fx-base: #5cb85c;" : ""); if (sel) elegidos.add(a); else elegidos.remove(a); });
            }
            g.add(b, a.numero() - 1, a.fila() - 'A');
        }
        Label pantalla = new Label("━━━━━━━━  PANTALLA  ━━━━━━━━"); pantalla.setMaxWidth(Double.MAX_VALUE); pantalla.setAlignment(Pos.CENTER);
        Label leyenda = new Label("Gris: disponible   ·   Verde: elegido   ·   Rojo: ocupado");
        Dialog<ButtonType> d = new Dialog<>(); d.setTitle(f.pelicula().titulo() + " · " + f.sala().nombre());
        d.getDialogPane().setContent(Ui.columna(pantalla, g, leyenda)); d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> { if (elegidos.isEmpty()) carrito.asientos.remove(f); else carrito.asientos.put(f, elegidos); });
    }
}
