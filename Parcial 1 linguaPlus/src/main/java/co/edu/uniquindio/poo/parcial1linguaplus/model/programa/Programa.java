package co.edu.uniquindio.poo.parcial1linguaplus.model.programa;

import co.edu.uniquindio.poo.parcial1linguaplus.model.EstadoPrograma;
import co.edu.uniquindio.poo.parcial1linguaplus.model.Modalidad;

public abstract class Programa {
    protected String codigo;
    protected String nombre;
    protected String idioma;
    protected String descripcion;
    protected int duracionMeses;
    protected double valorMensual;
    protected EstadoPrograma estado;
    protected Modalidad modalidad;

    //Valor base del programa
    public double calcularValorBase() {
        return valorMensual * duracionMeses;
    }

    public String getCodigo() { return codigo; }

    public String getNombre() { return nombre; }

    public String getIdioma() { return idioma; }

    public String getDescripcion() { return descripcion; }

    public int getDuracionMeses() { return duracionMeses; }

    public double getValorMensual() { return valorMensual; }

    public EstadoPrograma getEstado() { return estado; }

    public void setEstado(EstadoPrograma estado) { this.estado = estado; }

    public Modalidad getModalidad() { return modalidad; }

    @Override
    public String toString() {
        return "Programa{" +
                "codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", idioma='" + idioma + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", duracionMeses=" + duracionMeses +
                ", valorMensual=" + valorMensual +
                ", estado=" + estado +
                ", modalidad=" + modalidad +
                '}';
    }
}
