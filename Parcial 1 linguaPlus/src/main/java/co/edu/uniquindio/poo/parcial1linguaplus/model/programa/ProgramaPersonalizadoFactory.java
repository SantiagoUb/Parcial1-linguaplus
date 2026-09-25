package co.edu.uniquindio.poo.parcial1linguaplus.model.programa;

/*
 * Factory Method que, internamente, delega la construccion en el Builder
 * de ProgramaPersonalizado. Los dos patrones colaboran: el Factory Method
 * decide QUE se crea, el Builder decide COMO se arma paso a paso.
 */

public class ProgramaPersonalizadoFactory extends ProgramaFactory{

    @Override
    public Programa crearPrograma(DatosPrograma d) {
        return new ProgramaPersonalizado.Builder()
                .codigo(d.codigo)
                .nombre(d.nombre)
                .idioma(d.idioma)
                .descripcion(d.descripcion)
                .duracionMeses(d.duracionMeses)
                .valorMensual(d.valorMensual)
                .estado(d.estado)
                .modalidad(d.modalidad)
                .cantidadSesionesTutor(d.cantidadSesionesTutor)
                .nivelIdiomaRequerido(d.nivelIdiomaRequerido)
                .objetivosEstudiante(d.objetivosEstudiante)
                .build();
    }
}
