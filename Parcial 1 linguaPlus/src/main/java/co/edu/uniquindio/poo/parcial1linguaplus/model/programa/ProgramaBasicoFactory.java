package co.edu.uniquindio.poo.parcial1linguaplus.model.programa;

public class ProgramaBasicoFactory extends ProgramaFactory{

    @Override
    public Programa crearPrograma(DatosPrograma d) {
        return new ProgramaBasico(d.codigo, d.nombre, d.idioma, d.descripcion, d.duracionMeses, d.valorMensual,
                d.estado, d.modalidad);
    }
}
