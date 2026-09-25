package co.edu.uniquindio.poo.parcial1linguaplus.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * Controlador de la ventana principal. Actua como un enrutador simple:
 * cada boton del menu lateral carga la vista correspondiente dentro del
 * StackPane central, sin necesidad de abrir ventanas nuevas.
 */
public class MainController {

    @FXML
    private StackPane contenedorPrincipal;

    @FXML
    public void initialize() {
        mostrarEstudiantes();
    }

    @FXML
    private void mostrarEstudiantes() {
        cargarVista("estudiante-view.fxml");
    }

    @FXML
    private void mostrarDocentes() {
        cargarVista("docente-view.fxml");
    }

    @FXML
    private void mostrarProgramas() {
        cargarVista("programa-view.fxml");
    }

    @FXML
    private void mostrarServicios() {
        cargarVista("servicio-view.fxml");
    }

    @FXML
    private void mostrarMatricula() {
        cargarVista("matricula-view.fxml");
    }

    @FXML
    private void mostrarConsultas() {
        cargarVista("consultas-view.fxml");
    }

    private void cargarVista(String nombreArchivo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/co/edu/uniquindio/poo/parcial1linguaplus/view/" + nombreArchivo));
            Parent vista = loader.load();
            contenedorPrincipal.getChildren().setAll(vista);
        } catch (IOException e) {
            mostrarError("No se pudo cargar la vista: " + nombreArchivo, e.getMessage());
        }
    }

    private void mostrarError(String cabecera, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(cabecera);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
