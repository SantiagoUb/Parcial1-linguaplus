package co.edu.uniquindio.poo.parcial1linguaplus.model;

import co.edu.uniquindio.poo.parcial1linguaplus.model.programa.Programa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Matricula {
    //atributos obligatorios
    private final int numeroMatricula;
    private final Estudiante estudiante;
    private final Programa programa;
    private final LocalDate fechaInicio;
    //opcionales
    private final Docente docenteTutor;
    private final List<ServicioAdicional> serviciosAdicionales;
    private final double descuentoPorcentaje;
    private final String observaciones;

    private Matricula(Builder builder) {
        this.numeroMatricula = builder.numeroMatricula;
        this.estudiante = builder.estudiante;
        this.programa = builder.programa;
        this.fechaInicio = builder.fechaInicio;
        this.docenteTutor = builder.docenteTutor;
        this.serviciosAdicionales = Collections.unmodifiableList(builder.serviciosAdicionales);
        this.descuentoPorcentaje = builder.descuentoPorcentaje;
        this.observaciones = builder.observaciones;
    }

    public static Builder builder(int numeroMatricula, Estudiante estudiante, Programa programa, LocalDate fechaInicio) {
        return new Builder(numeroMatricula, estudiante, programa, fechaInicio);
    }

    public double calcularValorTotal(){
        double valorPrograma = programa.calcularValorBase();
        double valorServicios = serviciosAdicionales.stream().mapToDouble(ServicioAdicional::getPrecio).sum();
        double descuento = valorPrograma * (descuentoPorcentaje/ 100.0);
        return (valorPrograma - descuento) + valorServicios;
    }

    public int getNumeroMatricula() {
        return numeroMatricula;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public Programa getPrograma() {
        return programa;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public Docente getDocenteTutor() {
        return docenteTutor;
    }

    public List<ServicioAdicional> getServiciosAdicionales() {
        return serviciosAdicionales;
    }

    public double getDescuentoPorcentaje() {
        return descuentoPorcentaje;
    }

    public String getObservaciones() {
        return observaciones;
    }

    @Override
    public String toString() {
        return "Matricula{" +
                "numeroMatricula=" + numeroMatricula +
                ", estudiante=" + estudiante.getNombreCompleto() +
                ", programa=" + programa.getNombre() +
                ", fechaInicio=" + fechaInicio +
                ", docenteTutor=" + docenteTutor +
                ", serviciosAdicionales=" + serviciosAdicionales +
                ", descuentoPorcentaje=" + descuentoPorcentaje +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }

    public static class Builder {
        private final int numeroMatricula;
        private final Estudiante estudiante;
        private final Programa programa;
        private final LocalDate fechaInicio;

        private Docente docenteTutor;
        private final List<ServicioAdicional> serviciosAdicionales = new ArrayList<>();
        private double descuentoPorcentaje = 0;
        private String observaciones = "";

        private Builder(int numeroMatricula, Estudiante estudiante, Programa programa, LocalDate fechaInicio) {
            if(estudiante == null){
                throw  new IllegalArgumentException("la matricula requiere un estudiante");
            }
            if(programa == null){
                throw  new IllegalArgumentException("la matricula no puede existir sin un programa");
            }
            if(fechaInicio == null){
                throw new IllegalArgumentException("la matricula requiere un fecha de inicio");
            }
            this.numeroMatricula = numeroMatricula;
            this.estudiante = estudiante;
            this.programa = programa;
            this.fechaInicio = fechaInicio;

        }

        public Builder conDocente(Docente docente){
            this.docenteTutor = docente;
            return this;
        }

        public Builder conServicioAdicional(ServicioAdicional servicioAdicional){
            this.serviciosAdicionales.add(servicioAdicional);
            return this;
        }

        public Builder conDescuentoPorcentaje(double descuentoPorcentaje){
            this.descuentoPorcentaje = descuentoPorcentaje;
            return this;
        }
        public Builder conObservaciones(String observaciones){
            this.observaciones = observaciones;
            return this;
        }
        public Matricula build(){
            if(descuentoPorcentaje > 30){
                throw new IllegalArgumentException("el descuento mo puede superar el 30% del valor del programa");
            }
            if(descuentoPorcentaje < 0){
                throw new IllegalArgumentException("el descuento no puede ser negativo");
            }
            return new Matricula(this);
        }

    }
}
