package co.edu.uniquindio.poo.parcial1linguaplus.controller;

import co.edu.uniquindio.poo.parcial1linguaplus.model.Academia;
import co.edu.uniquindio.poo.parcial1linguaplus.model.Docente;
import co.edu.uniquindio.poo.parcial1linguaplus.model.Estudiante;
import co.edu.uniquindio.poo.parcial1linguaplus.model.Matricula;
import co.edu.uniquindio.poo.parcial1linguaplus.model.ServicioAdicional;
import co.edu.uniquindio.poo.parcial1linguaplus.model.comprobante.ComprobanteExelFactory;
import co.edu.uniquindio.poo.parcial1linguaplus.model.comprobante.ComprobanteFactory;
import co.edu.uniquindio.poo.parcial1linguaplus.model.comprobante.ComprobantePdfFactory;
import co.edu.uniquindio.poo.parcial1linguaplus.model.programa.Programa;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.time.LocalDate;

public class MatriculaController {

    @FXML private ComboBox<Estudiante> comboEstudiante;
    @FXML private ComboBox<Programa> comboPrograma;
    @FXML private ComboBox<Docente> comboDocente;
    @FXML private DatePicker datePickerInicio;
    @FXML private ListView<ServicioAdicional> listServicios;
    @FXML private TextField txtDescuento;
    @FXML private TextField txtObservaciones;
    @FXML private ComboBox<String> comboFormato;
    @FXML private TextArea areaResultado;

    @FXML private TableView<Matricula> tablaMatriculas;
    @FXML private TableColumn<Matricula, Integer> colNumero;
    @FXML private TableColumn<Matricula, String> colEstudiante;
    @FXML private TableColumn<Matricula, String> colPrograma;
    @FXML private TableColumn<Matricula, LocalDate> colFecha;
    @FXML private TableColumn<Matricula, Double> colTotal;

    private final Academia academia = Academia.getInstance();
    private final ObservableList<Matricula> datos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        comboEstudiante.setItems(FXCollections.observableArrayList(academia.getEstudiantes()));
        comboPrograma.setItems(FXCollections.observableArrayList(academia.getProgramas()));
        comboDocente.setItems(FXCollections.observableArrayList(academia.getDocentes()));
        listServicios.setItems(FXCollections.observableArrayList(academia.getServiciosAdicionales()));
        listServicios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        comboFormato.setItems(FXCollections.observableArrayList("PDF", "EXCEL"));
        comboFormato.getSelectionModel().selectFirst();
        datePickerInicio.setValue(LocalDate.now());

        comboEstudiante.setConverter(new StringConverter<>() {
            @Override
            public String toString(Estudiante e) {
                return e == null ? "" : e.getNombreCompleto() + " (" + e.getDocumentoIdentidad() + ")";
            }

            @Override
            public Estudiante fromString(String s) {
                return null;
            }
        });
        comboPrograma.setConverter(new StringConverter<>() {
            @Override
            public String toString(Programa p) {
                return p == null ? "" : p.getNombre() + " [" + p.getCodigo() + "]";
            }

            @Override
            public Programa fromString(String s) {
                return null;
            }
        });
        comboDocente.setConverter(new StringConverter<>() {
            @Override
            public String toString(Docente d) {
                return d == null ? "" : d.getNombre();
            }

            @Override
            public Docente fromString(String s) {
                return null;
            }
        });
        listServicios.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ServicioAdicional item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre() + " ($" + item.getPrecio() + ")");
            }
        });

        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroMatricula"));
        colEstudiante.setCellValueFactory(dato ->
                new SimpleStringProperty(dato.getValue().getEstudiante().getNombreCompleto()));
        colPrograma.setCellValueFactory(dato ->
                new SimpleStringProperty(dato.getValue().getPrograma().getNombre()));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colTotal.setCellValueFactory(dato ->
                new SimpleDoubleProperty(dato.getValue().calcularValorTotal()).asObject());

        datos.setAll(academia.getMatriculas());
        tablaMatriculas.setItems(datos);
    }

    @FXML
    private void registrarMatricula() {
        try {
            Estudiante estudiante = comboEstudiante.getValue();
            Programa programa = comboPrograma.getValue();
            LocalDate fecha = datePickerInicio.getValue();
            if (estudiante == null || programa == null || fecha == null) {
                mostrarAlerta("Estudiante, programa y fecha de inicio son obligatorios.");
                return;
            }

            double descuento = txtDescuento.getText().isBlank() ? 0 : Double.parseDouble(txtDescuento.getText().trim());
            int numero = academia.generarNumeroMatricula();

            Matricula.Builder builder = Matricula.builder(numero, estudiante, programa, fecha)
                    .conDescuentoPorcentaje(descuento)
                    .conObservaciones(txtObservaciones.getText().trim());

            if (comboDocente.getValue() != null) {
                builder.conDocente(comboDocente.getValue());
            }
            for (ServicioAdicional servicio : listServicios.getSelectionModel().getSelectedItems()) {
                builder.conServicioAdicional(servicio);
            }

            Matricula matricula = builder.build();
            academia.registrarMatricula(matricula);
            datos.add(matricula);

            ComprobanteFactory factory = "PDF".equals(comboFormato.getValue())
                    ? new ComprobantePdfFactory()
                    : new ComprobanteExelFactory();
            areaResultado.setText(academia.emitirMatricula(matricula, factory));

            limpiarFormulario();
        } catch (NumberFormatException e) {
            mostrarAlerta("El descuento debe ser un numero valido.");
        } catch (IllegalArgumentException e) {
            mostrarAlerta(e.getMessage());
        }
    }

    @FXML
    private void limpiarFormulario() {
        comboEstudiante.getSelectionModel().clearSelection();
        comboPrograma.getSelectionModel().clearSelection();
        comboDocente.getSelectionModel().clearSelection();
        listServicios.getSelectionModel().clearSelection();
        txtDescuento.clear();
        txtObservaciones.clear();
        datePickerInicio.setValue(LocalDate.now());
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
