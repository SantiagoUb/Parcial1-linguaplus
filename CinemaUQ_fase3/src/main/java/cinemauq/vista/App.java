package cinemauq.vista;
import cinemauq.ContextoCine;
import cinemauq.controlador.LoginController;
import java.nio.file.*;
import javafx.application.Application;
import javafx.scene.*;
import javafx.stage.Stage;
public class App extends Application {
    static Stage stage;
    @Override public void start(Stage s) throws Exception {
        stage = s; Path dir = Path.of(System.getProperty("user.home"), ".cinemauq"); Files.createDirectories(dir);
        Sesion.ctx = ContextoCine.iniciar(dir.resolve("cinemauq.dat"));
        s.setTitle("CinemaUQ"); irA(new LoginController().vista());
    }
    public static void irA(Parent raiz) { stage.setScene(new Scene(raiz, 1180, 740)); stage.show(); }
    public static Stage ventana() { return stage; }
    public static void main(String[] args) { launch(args); }
}
