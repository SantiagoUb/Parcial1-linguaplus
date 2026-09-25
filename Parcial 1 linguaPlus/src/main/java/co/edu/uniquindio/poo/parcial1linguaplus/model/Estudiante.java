package co.edu.uniquindio.poo.parcial1linguaplus.model;

import java.time.LocalDate;

public class Estudiante {
    private final String documentoIdentidad;
    private String nombreCompleto;
    private String telefono;
    private String correoElectronico;
    private int edad;
    private final LocalDate fechaRegistro;

    public Estudiante(String documentoIdentidad, String nombreCompleto, String telefono,
                      String correoElectronico, int edad) {
        this.documentoIdentidad = documentoIdentidad;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
        this.edad = edad;
        this.fechaRegistro = LocalDate.now();
    }

    public String getDocumentoIdentidad() { return documentoIdentidad; }

    public String getNombreCompleto() { return nombreCompleto; }

    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getTelefono() { return telefono; }

    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreoElectronico() { return correoElectronico; }

    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public int getEdad() { return edad; }

    public void setEdad(int edad) { this.edad = edad; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }

    @Override
    public String toString() {
        return "Estudiante{" +
                "documentoIdentidad='" + documentoIdentidad + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", telefono='" + telefono + '\'' +
                ", correoElectronico='" + correoElectronico + '\'' +
                ", edad=" + edad +
                ", fechaRegistro=" + fechaRegistro +
                '}';
    }
}
