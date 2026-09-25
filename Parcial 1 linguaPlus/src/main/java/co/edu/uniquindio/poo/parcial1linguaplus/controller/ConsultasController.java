package co.edu.uniquindio.poo.parcial1linguaplus.controller;

import co.edu.uniquindio.poo.parcial1linguaplus.model.Academia;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.Optional;

public class ConsultasController {

    @FXML private TextField txtTelefono;
    @FXML private Label lblResultadoTelefono;

    @FXML private DatePicker datePickerDesde;
    @FXML private DatePicker datePickerHasta;
    @FXML private Label lblIngresos;

    private final Academia academia = Academia.getInstance();

    @FXML
    private void buscarPorTelefono() {
        String telefono = txtTelefono.getText().trim();
        if (telefono.isBlank()) {
            lblResultadoTelefono.setText("Ingresa un numero de telefono.");
            return;
        }
        Optional<Academia.ResultadoConcultaTelefono> resultado = academia.buscarEstudiantePorTelefono(telefono);
        if (resultado.isEmpty()) {
            lblResultadoTelefono.setText("No se encontro ningun estudiante con ese telefono.");
            return;
        }
        Academia.ResultadoConcultaTelefono r = resultado.get();
        lblResultadoTelefono.setText("Estudiante: " + r.estudiante.getNombreCompleto()
                + (r.esNumeroPrefecto ? " — su numero es un numero perfecto." : " — su numero no es un numero perfecto."));
    }

    @FXML
    private void calcularIngresos() {
        LocalDate desde = datePickerDesde.getValue();
        LocalDate hasta = datePickerHasta.getValue();
        if (desde == null || hasta == null) {
            lblIngresos.setText("Selecciona ambas fechas.");
            return;
        }
        if (desde.isAfter(hasta)) {
            lblIngresos.setText("La fecha 'desde' no puede ser posterior a 'hasta'.");
            return;
        }
        double total = academia.calcularIngresosPorPeriodo(desde, hasta);
        lblIngresos.setText(String.format("Ingresos del periodo: $%.2f", total));
    }
}
