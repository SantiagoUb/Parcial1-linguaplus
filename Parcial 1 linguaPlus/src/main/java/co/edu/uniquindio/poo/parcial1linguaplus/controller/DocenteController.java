package co.edu.uniquindio.poo.parcial1linguaplus.controller;

import co.edu.uniquindio.poo.parcial1linguaplus.model.Academia;
import co.edu.uniquindio.poo.parcial1linguaplus.model.Docente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class DocenteController {

    @FXML private TextField txtIdentificacion;
    @FXML private TextField txtNombre;
    @FXML private TextField txtIdioma;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtTarifa;

    @FXML private TableView<Docente> tablaDocentes;
    @FXML private TableColumn<Docente, String> colIdentificacion;
    @FXML private TableColumn<Docente, String> colNombre;
    @FXML private TableColumn<Docente, String> colIdioma;
    @FXML private TableColumn<Docente, String> colTelefono;
    @FXML private TableColumn<Docente, Double> colTarifa;

    private final Academia academia = Academia.getInstance();
    private final ObservableList<Docente> datos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colIdentificacion.setCellValueFactory(new PropertyValueFactory<>("identificacion"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colIdioma.setCellValueFactory(new PropertyValueFactory<>("idiomaEspecialidad"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colTarifa.setCellValueFactory(new PropertyValueFactory<>("tarifaPorSesion"));

        datos.setAll(academia.getDocentes());
        tablaDocentes.setItems(datos);
    }

    @FXML
    private void registrarDocente() {
        try {
            if (txtIdentificacion.getText().isBlank() || txtNombre.getText().isBlank()) {
                mostrarAlerta("La identificacion y el nombre son obligatorios.");
                return;
            }
            double tarifa = Double.parseDouble(txtTarifa.getText().trim());
            Docente docente = new Docente(txtIdentificacion.getText().trim(), txtNombre.getText().trim(),
                    txtIdioma.getText().trim(), txtTelefono.getText().trim(), tarifa);
            academia.registrarDocente(docente);
            datos.add(docente);
            limpiarFormulario();
        } catch (NumberFormatException e) {
            mostrarAlerta("La tarifa por sesion debe ser un numero valido.");
        }
    }

    @FXML
    private void limpiarFormulario() {
        txtIdentificacion.clear();
        txtNombre.clear();
        txtIdioma.clear();
        txtTelefono.clear();
        txtTarifa.clear();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
