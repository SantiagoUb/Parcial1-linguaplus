package co.edu.uniquindio.poo.parcial1linguaplus.model.programa;

import co.edu.uniquindio.poo.parcial1linguaplus.model.EstadoPrograma;
import co.edu.uniquindio.poo.parcial1linguaplus.model.Modalidad;

/*
 * Creator del patron Factory Method. Cada subclase concreta decide que tipo
 * de Programa se instancia (Basico, Intensivo o Personalizado) sin que el
 * codigo cliente tenga que conocer las clases concretas.
 */

public abstract class ProgramaFactory {

    //Factory method que cada subclase implementa
    public abstract Programa crearPrograma(DatosPrograma datos);

    //Operacion de alto nivel que usa el factory method son conocer la clase concreta
    public Programa registrarPrograma(DatosPrograma datos) {
        Programa programa = crearPrograma(datos);
        System.out.println("Programa registrado ->" +programa);
        return programa;
    }

    //DTO simple para no explotar el metodo con muchos parametros
    public static class DatosPrograma {
        public String codigo;
        public String nombre;
        public String idioma;
        public String descripcion;
        public int duracionMeses;
        public double valorMensual;
        public EstadoPrograma estado = EstadoPrograma.ACTIVO;
        public Modalidad modalidad;
        // solo aplica a programas personalizados
        public int cantidadSesionesTutor;
        public String nivelIdiomaRequerido;
        public String objetivosEstudiante;
    }

}
