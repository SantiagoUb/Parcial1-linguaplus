package co.edu.uniquindio.poo.parcial1linguaplus.controller;

import co.edu.uniquindio.poo.parcial1linguaplus.model.Academia;
import co.edu.uniquindio.poo.parcial1linguaplus.model.Estudiante;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class EstudianteController {

    @FXML private TextField txtDocumento;
    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtEdad;

    @FXML private TableView<Estudiante> tablaEstudiantes;
    @FXML private TableColumn<Estudiante, String> colDocumento;
    @FXML private TableColumn<Estudiante, String> colNombre;
    @FXML private TableColumn<Estudiante, String> colTelefono;
    @FXML private TableColumn<Estudiante, String> colCorreo;
    @FXML private TableColumn<Estudiante, Integer> colEdad;

    private final Academia academia = Academia.getInstance();
    private final ObservableList<Estudiante> datos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colDocumento.setCellValueFactory(new PropertyValueFactory<>("documentoIdentidad"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));

        datos.setAll(academia.getEstudiantes());
        tablaEstudiantes.setItems(datos);

        tablaEstudiantes.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                txtDocumento.setText(seleccionado.getDocumentoIdentidad());
                txtNombre.setText(seleccionado.getNombreCompleto());
                txtTelefono.setText(seleccionado.getTelefono());
                txtCorreo.setText(seleccionado.getCorreoElectronico());
                txtEdad.setText(String.valueOf(seleccionado.getEdad()));
            }
        });
    }

    @FXML
    private void registrarEstudiante() {
        try {
            if (txtDocumento.getText().isBlank() || txtNombre.getText().isBlank()) {
                mostrarAlerta(Alert.AlertType.WARNING, "El documento y el nombre son obligatorios.");
                return;
            }
            int edad = Integer.parseInt(txtEdad.getText().trim());
            Estudiante estudiante = new Estudiante(txtDocumento.getText().trim(), txtNombre.getText().trim(),
                    txtTelefono.getText().trim(), txtCorreo.getText().trim(), edad);
            academia.registrarEstudiante(estudiante);
            datos.add(estudiante);
            limpiarFormulario();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Estudiante registrado correctamente.");
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "La edad debe ser un numero valido.");
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    /** Actualiza en sitio el estudiante seleccionado en la tabla (los datos son mutables salvo el documento). */
    @FXML
    private void actualizarSeleccionado() {
        Estudiante seleccionado = tablaEstudiantes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un estudiante de la tabla para actualizar.");
            return;
        }
        try {
            seleccionado.setNombreCompleto(txtNombre.getText().trim());
            seleccionado.setTelefono(txtTelefono.getText().trim());
            seleccionado.setCorreoElectronico(txtCorreo.getText().trim());
            seleccionado.setEdad(Integer.parseInt(txtEdad.getText().trim()));
            tablaEstudiantes.refresh();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Estudiante actualizado correctamente.");
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "La edad debe ser un numero valido.");
        }
    }

    @FXML
    private void limpiarFormulario() {
        txtDocumento.clear();
        txtNombre.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        txtEdad.clear();
        tablaEstudiantes.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
