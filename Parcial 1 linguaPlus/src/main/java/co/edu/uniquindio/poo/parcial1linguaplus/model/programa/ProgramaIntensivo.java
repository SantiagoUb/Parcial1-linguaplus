package co.edu.uniquindio.poo.parcial1linguaplus.model.programa;

import co.edu.uniquindio.poo.parcial1linguaplus.model.EstadoPrograma;
import co.edu.uniquindio.poo.parcial1linguaplus.model.Modalidad;

public class ProgramaIntensivo extends Programa{

    private static final double RECARGO_INTENSIVO = 1.20; //20% adicional

    public ProgramaIntensivo(String codigo, String nombre, String idioma, String descripcion,
                             int duracionMeses, double valorMensual,
                             EstadoPrograma estado, Modalidad modalidad) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.idioma = idioma;
        this.descripcion = descripcion;
        this.duracionMeses = duracionMeses;
        this.valorMensual = valorMensual;
        this.estado = estado;
        this.modalidad = modalidad;
    }

    @Override
    public double calcularValorBase() {
        return super.calcularValorBase() * RECARGO_INTENSIVO;
    }

    public String getTipo() {
        return "intensivo";
    }

}
