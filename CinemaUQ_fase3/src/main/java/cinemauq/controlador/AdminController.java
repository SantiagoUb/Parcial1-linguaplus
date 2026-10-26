package cinemauq.controlador;
import cinemauq.vista.*;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
public class AdminController {
    public Parent vista() {
        Button salir = new Button("Cerrar sesión"); salir.setOnAction(e -> { Sesion.usuario = null; App.irA(new LoginController().vista()); });
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        HBox top = new HBox(10, Ui.titulo("CinemaUQ · Panel administrativo"), sp, salir); top.setPadding(new Insets(10));
        BorderPane bp = new BorderPane(); bp.setTop(top);
        bp.setCenter(Ui.pestanas(List.of(new PeliculasAdmin(), new SalasAdmin(), new FuncionesAdmin(), new ConfiteriaAdmin(), new PromocionesAdmin(),
            new ClientesAdmin(), new ComprasAdmin(), new RecargasAdmin(), new ReportesAdmin(), new EstadisticasAdmin())));
        return bp;
    }
}
