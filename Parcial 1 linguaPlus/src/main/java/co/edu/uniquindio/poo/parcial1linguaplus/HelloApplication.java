package co.edu.uniquindio.poo.parcial1linguaplus;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("view/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 950, 650);
        stage.setTitle("LinguaPlus - Academia de idiomas");
        stage.setScene(scene);
        stage.show();
    }
}