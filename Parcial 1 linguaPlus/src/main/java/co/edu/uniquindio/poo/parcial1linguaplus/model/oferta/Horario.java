package co.edu.uniquindio.poo.parcial1linguaplus.model.oferta;

public class Horario implements Cloneable{
    private final String dia;
    private final int horaInicio;
    private final int horaFin;

    public Horario(String dia, int horaInicio, int horaFin) {
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    @Override
    public Horario clone() {
        return new Horario(dia, horaInicio, horaFin);
    }

    @Override
    public String toString() {
        return "Horario{" +
                "dia='" + dia + '\'' +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                '}';
    }

    public String getDia() {
        return dia;
    }

    public int getHoraInicio() {
        return horaInicio;
    }

    public int getHoraFin() {
        return horaFin;
    }
}
