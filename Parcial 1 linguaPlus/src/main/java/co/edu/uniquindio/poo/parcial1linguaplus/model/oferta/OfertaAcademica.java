package co.edu.uniquindio.poo.parcial1linguaplus.model.oferta;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*
 * Prototype: la oferta base (programas, horarios, tarifas) se arma una sola vez,
 * consultando disponibilidad de docentes, salones y tarifas vigentes. Cada nuevo
 * periodo academico se crea CLONANDO esa oferta y cambiando solo fecha y cupos,
 * en lugar de rehacer todo el proceso de armado desde cero.
 */
public class OfertaAcademica implements Cloneable{
    private String periodo;
    private LocalDate fechaApertura;
    private List<Horario> horarios;
    private List<CupoPrograma> cupos;

    public OfertaAcademica(String periodo, LocalDate fechaApertura) {
        this.periodo = periodo;
        this.fechaApertura = fechaApertura;
        this.horarios = new ArrayList<>();
        this.cupos = new ArrayList<>();
    }


    /*
     * Clona profundamente la oferta: mismos programas, horarios y tarifas base,
     * pero con una lista de cupos independiente y reiniciada. Un clon nunca
     * comparte estado de ocupacion con el original ni con otros clones.
     */
    @Override
    public OfertaAcademica clone() {
        OfertaAcademica copia;
        try {
            copia = (OfertaAcademica) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
        copia.horarios = new ArrayList<>(horarios);
        for (Horario horario : horarios) {
            copia.horarios.add(horario.clone());
        }
        copia.cupos = new ArrayList<>(cupos);
        for (CupoPrograma cupo : cupos) {
            copia.cupos.add(cupo.clone());
        }
        return copia;
    }

    /* Crea un nuevo periodo a partir de esta oferta prototipo, cambiando solo lo que varia. */

    public OfertaAcademica nuervoPeriodo(String nuevoPeriodo, LocalDate nuevaFecha) {
        OfertaAcademica clon = this.clone();
        clon.periodo = nuevoPeriodo;
        clon.fechaApertura = nuevaFecha;
        return clon;
    }

    public void agregarHorario(Horario horario){
        horarios.add(horario);
    }
    public void agregarCupo(CupoPrograma cupo){
        cupos.add(cupo);
    }

    public String getPeriodo() {
        return periodo;
    }

    public LocalDate getFechaApertura() {
        return fechaApertura;
    }

    public List<Horario> getHorarios() {
        return horarios;
    }

    public List<CupoPrograma> getCupos() {
        return cupos;
    }

    @Override
    public String toString() {
        return "OfertaAcademica{" +
                "periodo='" + periodo + '\'' +
                ", fechaApertura=" + fechaApertura +
                ", horarios=" + horarios +
                ", cupos=" + cupos +
                '}';
    }
}
