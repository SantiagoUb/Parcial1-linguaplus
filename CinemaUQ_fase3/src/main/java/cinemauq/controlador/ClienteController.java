package cinemauq.controlador;
import cinemauq.modelo.Cliente;
import cinemauq.vista.*;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
public class ClienteController {
    public Parent vista() {
        Cliente c = (Cliente) Sesion.usuario; Carrito carrito = new Carrito(c);
        Button salir = new Button("Cerrar sesión"); salir.setOnAction(e -> { Sesion.usuario = null; App.irA(new LoginController().vista()); });
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        HBox top = new HBox(10, Ui.titulo("CinemaUQ · Hola, " + c.nombre()), sp, salir); top.setPadding(new Insets(10));
        BorderPane bp = new BorderPane(); bp.setTop(top);
        bp.setCenter(Ui.pestanas(List.of(new CarteleraPantalla(carrito), new CompraPantalla(carrito), new TarjetaPantalla(), new PuntosPantalla(), new HistorialPantalla())));
        return bp;
    }
}
