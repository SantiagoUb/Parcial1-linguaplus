package cinemauq.controlador;
import cinemauq.excepcion.ValidacionException;
import cinemauq.modelo.Rol;
import cinemauq.vista.*;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
/** RF-001 y RF-002. */
public class LoginController {
    public Parent vista() {
        TextField email = Ui.campo("Correo"); PasswordField pass = new PasswordField(); pass.setPromptText("Contraseña");
        Button entrar = new Button("Ingresar"), registro = new Button("Crear cuenta");
        entrar.setDefaultButton(true);
        entrar.setOnAction(e -> Ui.accion(() -> {
            var u = Sesion.ctx.usuarios.autenticar(email.getText(), pass.getText()); Sesion.usuario = u;
            App.irA(u.rol() == Rol.ADMINISTRADOR ? new AdminController().vista() : new ClienteController().vista());
        }));
        registro.setOnAction(e -> registrar());
        Label demo = new Label("Demo: admin@cinemauq.co / admin123   ·   cliente1@cinemauq.co / clave123"); demo.setStyle("-fx-text-fill: gray;");
        VBox box = new VBox(12, Ui.titulo("CinemaUQ"), email, pass, Ui.fila(entrar, registro), demo);
        box.setMaxWidth(360); box.setAlignment(Pos.CENTER); StackPane sp = new StackPane(box); sp.setPadding(new Insets(40)); return sp;
    }
    private void registrar() {
        TextField nombre = Ui.campo("Nombre completo"), email = Ui.campo("Correo"); PasswordField p1 = new PasswordField(), p2 = new PasswordField();
        p1.setPromptText("Contraseña (mín. 6)"); p2.setPromptText("Repetir contraseña");
        Dialog<ButtonType> d = new Dialog<>(); d.setTitle("Registro de cliente"); d.getDialogPane().setContent(Ui.columna(nombre, email, p1, p2));
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.showAndWait().filter(b -> b == ButtonType.OK).ifPresent(b -> Ui.accion(() -> {
            if (!p1.getText().equals(p2.getText())) throw new ValidacionException("Las contraseñas no coinciden");
            Sesion.ctx.usuarios.registrar(nombre.getText(), email.getText(), p1.getText()); Ui.info("Cuenta creada. Ya puede iniciar sesión.");
        }));
    }
}
