package co.edu.uniquindio.poo.parcial1linguaplus.model.comprobante;

import co.edu.uniquindio.poo.parcial1linguaplus.model.Matricula;

public class ComprobanteExel extends Comprobante{

    public ComprobanteExel(Matricula matricula){
        super(matricula);
    }

    @Override
    public String generar() {
        return String.format("Numero;Estudiante;Programa;Total%n%d;%s;%s;%.2f",
                matricula.getNumeroMatricula(), matricula.getEstudiante().getNombreCompleto(),
                matricula.getPrograma().getNombre(), matricula.calcularValorTotal());
    }

    @Override
    public String getFormato() {
        return "EXCEL";
    }
}
