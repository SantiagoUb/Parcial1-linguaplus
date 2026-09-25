package co.edu.uniquindio.poo.parcial1linguaplus.controller;

import co.edu.uniquindio.poo.parcial1linguaplus.model.Academia;
import co.edu.uniquindio.poo.parcial1linguaplus.model.EstadoPrograma;
import co.edu.uniquindio.poo.parcial1linguaplus.model.Modalidad;
import co.edu.uniquindio.poo.parcial1linguaplus.model.programa.Programa;
import co.edu.uniquindio.poo.parcial1linguaplus.model.programa.ProgramaBasicoFactory;
import co.edu.uniquindio.poo.parcial1linguaplus.model.programa.ProgramaFactory;
import co.edu.uniquindio.poo.parcial1linguaplus.model.programa.ProgramaIntensivoFactory;
import co.edu.uniquindio.poo.parcial1linguaplus.model.programa.ProgramaPersonalizadoFactory;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador de registro de programas academicos. El ComboBox "Tipo de programa"
 * decide, en tiempo de ejecucion, cual ProgramaFactory (Factory Method) se usa
 * para construir la instancia concreta (Basico, Intensivo o Personalizado).
 */
public class ProgramaController {

    @FXML private ComboBox<String> comboTipo;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtIdioma;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtDuracion;
    @FXML private TextField txtValorMensual;
    @FXML private ComboBox<Modalidad> comboModalidad;
    @FXML private ComboBox<EstadoPrograma> comboEstado;

    @FXML private TitledPane paneDatosPersonalizado;
    @FXML private TextField txtSesionesTutor;
    @FXML private TextField txtNivelIdioma;
    @FXML private TextField txtObjetivos;

    @FXML private TableView<Programa> tablaProgramas;
    @FXML private TableColumn<Programa, String> colCodigo;
    @FXML private TableColumn<Programa, String> colNombre;
    @FXML private TableColumn<Programa, String> colIdioma;
    @FXML private TableColumn<Programa, Integer> colDuracion;
    @FXML private TableColumn<Programa, Double> colValorMensual;
    @FXML private TableColumn<Programa, EstadoPrograma> colEstado;
    @FXML private TableColumn<Programa, Double> colValorBase;

    private final Academia academia = Academia.getInstance();
    private final ObservableList<Programa> datos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        comboTipo.setItems(FXCollections.observableArrayList("Basico", "Intensivo", "Personalizado"));
        comboModalidad.setItems(FXCollections.observableArrayList(Modalidad.values()));
        comboEstado.setItems(FXCollections.observableArrayList(EstadoPrograma.values()));
        comboEstado.getSelectionModel().select(EstadoPrograma.ACTIVO);

        comboTipo.valueProperty().addListener((obs, viejo, nuevo) ->
                paneDatosPersonalizado.setDisable(!"Personalizado".equals(nuevo)));

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colIdioma.setCellValueFactory(new PropertyValueFactory<>("idioma"));
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracionMeses"));
        colValorMensual.setCellValueFactory(new PropertyValueFactory<>("valorMensual"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colValorBase.setCellValueFactory(dato ->
                new SimpleDoubleProperty(dato.getValue().calcularValorBase()).asObject());

        datos.setAll(academia.getProgramas());
        tablaProgramas.setItems(datos);
    }

    @FXML
    private void registrarPrograma() {
        try {
            String tipo = comboTipo.getValue();
            if (tipo == null || txtCodigo.getText().isBlank() || txtNombre.getText().isBlank()
                    || comboModalidad.getValue() == null) {
                mostrarAlerta("Tipo, codigo, nombre y modalidad son obligatorios.");
                return;
            }

            ProgramaFactory factory = switch (tipo) {
                case "Basico" -> new ProgramaBasicoFactory();
                case "Intensivo" -> new ProgramaIntensivoFactory();
                case "Personalizado" -> new ProgramaPersonalizadoFactory();
                default -> throw new IllegalArgumentException("Tipo de programa no reconocido");
            };

            ProgramaFactory.DatosPrograma datosPrograma = new ProgramaFactory.DatosPrograma();
            datosPrograma.codigo = txtCodigo.getText().trim();
            datosPrograma.nombre = txtNombre.getText().trim();
            datosPrograma.idioma = txtIdioma.getText().trim();
            datosPrograma.descripcion = txtDescripcion.getText().trim();
            datosPrograma.duracionMeses = Integer.parseInt(txtDuracion.getText().trim());
            datosPrograma.valorMensual = Double.parseDouble(txtValorMensual.getText().trim());
            datosPrograma.estado = comboEstado.getValue();
            datosPrograma.modalidad = comboModalidad.getValue();

            if ("Personalizado".equals(tipo)) {
                datosPrograma.cantidadSesionesTutor = txtSesionesTutor.getText().isBlank() ? 0
                        : Integer.parseInt(txtSesionesTutor.getText().trim());
                datosPrograma.nivelIdiomaRequerido = txtNivelIdioma.getText().trim();
                datosPrograma.objetivosEstudiante = txtObjetivos.getText().trim();
            }

            Programa programa = factory.registrarPrograma(datosPrograma);
            academia.registrarPrograma(programa);
            datos.add(programa);
            limpiarFormulario();
        } catch (NumberFormatException e) {
            mostrarAlerta("Duracion, valor mensual y sesiones deben ser numeros validos.");
        } catch (IllegalArgumentException e) {
            mostrarAlerta(e.getMessage());
        }
    }

    @FXML
    private void limpiarFormulario() {
        txtCodigo.clear();
        txtNombre.clear();
        txtIdioma.clear();
        txtDescripcion.clear();
        txtDuracion.clear();
        txtValorMensual.clear();
        txtSesionesTutor.clear();
        txtNivelIdioma.clear();
        txtObjetivos.clear();
        comboTipo.getSelectionModel().clearSelection();
        comboModalidad.getSelectionModel().clearSelection();
        comboEstado.getSelectionModel().select(EstadoPrograma.ACTIVO);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
