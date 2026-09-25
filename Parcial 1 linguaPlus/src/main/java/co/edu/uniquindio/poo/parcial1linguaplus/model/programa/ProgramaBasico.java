package co.edu.uniquindio.poo.parcial1linguaplus.model.programa;

import co.edu.uniquindio.poo.parcial1linguaplus.model.EstadoPrograma;
import co.edu.uniquindio.poo.parcial1linguaplus.model.Modalidad;

public class ProgramaBasico extends Programa {

    public ProgramaBasico(String codigo, String nombre, String idioma, String descripcion, int duracionMeses, double valorMensual,
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

    public String getTipo(){
        return "Basico";
    }
}
