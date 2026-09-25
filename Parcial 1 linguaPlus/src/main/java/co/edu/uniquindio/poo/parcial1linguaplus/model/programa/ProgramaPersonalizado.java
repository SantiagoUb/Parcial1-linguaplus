package co.edu.uniquindio.poo.parcial1linguaplus.model.programa;

import co.edu.uniquindio.poo.parcial1linguaplus.model.EstadoPrograma;
import co.edu.uniquindio.poo.parcial1linguaplus.model.Modalidad;

/*
 * Programa personalizado. Se construye SOLO a traves de su Builder interno,
 * porque requiere informacion adicional (sesiones con tutor, nivel, objetivos)
 * y varios campos opcionales que no tiene sentido pasar por un constructor largo.
 */

public class ProgramaPersonalizado extends Programa {

    private int cantidadSesionesTutor;
    private String nivelIdiomaRequerido;
    private String objetivosEstudiante;

    private ProgramaPersonalizado() {
        // construccion controlada unicamente por el Builder
    }

    @Override
    public double calcularValorBase() {
        double base = super.calcularValorBase();
        double costoTutoria = cantidadSesionesTutor * 15000;
        return base + costoTutoria;
    }

    public String getTipo(){
        return "Personalizado";
    }

    public int getCantidadSesionesTutor() { return cantidadSesionesTutor; }

    public String getNivelIdiomaRequerido() { return nivelIdiomaRequerido; }

    public String getObjetivosEstudiante() { return objetivosEstudiante; }

    public static class Builder {
        private final ProgramaPersonalizado programa = new ProgramaPersonalizado();

        public Builder codigo(String codigo){programa.codigo = codigo;return this;}
        public Builder nombre(String nombre){programa.nombre = nombre;return this;}
        public Builder idioma(String idioma) { programa.idioma = idioma; return this; }
        public Builder descripcion(String descripcion) { programa.descripcion = descripcion; return this; }
        public Builder duracionMeses(int meses) { programa.duracionMeses = meses; return this; }
        public Builder valorMensual(double valor) { programa.valorMensual = valor; return this; }
        public Builder estado(EstadoPrograma estado) { programa.estado = estado; return this; }
        public Builder modalidad(Modalidad modalidad) { programa.modalidad = modalidad; return this; }
        public Builder cantidadSesionesTutor(int sesiones) { programa.cantidadSesionesTutor = sesiones; return this; }
        public Builder nivelIdiomaRequerido(String nivel) { programa.nivelIdiomaRequerido = nivel; return this; }
        public Builder objetivosEstudiante(String objetivos) { programa.objetivosEstudiante = objetivos; return this; }

        public ProgramaPersonalizado build() {
            if(programa.codigo == null || programa.nombre == null){
                throw new IllegalArgumentException("El nombre y codigo de la programa no puede ser nulos");
            }
            if(programa.idioma == null){
                throw new IllegalArgumentException("el programa necesita un idioma");
            }
            if(programa.estado == null){
                programa.estado = EstadoPrograma.ACTIVO;
            }
        return programa;
        }
    }
}
