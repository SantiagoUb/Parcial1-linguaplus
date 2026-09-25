package co.edu.uniquindio.poo.parcial1linguaplus.controller;

import co.edu.uniquindio.poo.parcial1linguaplus.model.Academia;
import co.edu.uniquindio.poo.parcial1linguaplus.model.ServicioAdicional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ServicioAdicionalController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtPrecio;
    @FXML private CheckBox chkDisponible;

    @FXML private TableView<ServicioAdicional> tablaServicios;
    @FXML private TableColumn<ServicioAdicional, String> colCodigo;
    @FXML private TableColumn<ServicioAdicional, String> colNombre;
    @FXML private TableColumn<ServicioAdicional, String> colDescripcion;
    @FXML private TableColumn<ServicioAdicional, Double> colPrecio;
    @FXML private TableColumn<ServicioAdicional, Boolean> colDisponible;

    private final Academia academia = Academia.getInstance();
    private final ObservableList<ServicioAdicional> datos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colDisponible.setCellValueFactory(new PropertyValueFactory<>("disponible"));

        chkDisponible.setSelected(true);
        datos.setAll(academia.getServiciosAdicionales());
        tablaServicios.setItems(datos);
    }

    @FXML
    private void registrarServicio() {
        try {
            if (txtCodigo.getText().isBlank() || txtNombre.getText().isBlank()) {
                mostrarAlerta("El codigo y el nombre son obligatorios.");
                return;
            }
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            ServicioAdicional servicio = new ServicioAdicional(txtCodigo.getText().trim(), txtNombre.getText().trim(),
                    txtDescripcion.getText().trim(), precio, chkDisponible.isSelected());
            academia.registrarServicioAdicional(servicio);
            datos.add(servicio);
            limpiarFormulario();
        } catch (NumberFormatException e) {
            mostrarAlerta("El precio debe ser un numero valido.");
        }
    }

    @FXML
    private void limpiarFormulario() {
        txtCodigo.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txtPrecio.clear();
        chkDisponible.setSelected(true);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
