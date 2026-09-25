package co.edu.uniquindio.poo.parcial1linguaplus.model.comprobante;

import co.edu.uniquindio.poo.parcial1linguaplus.model.Matricula;

public abstract class Comprobante {
    protected final Matricula matricula;

    protected Comprobante(Matricula matricula) {
        this.matricula = matricula;
    }

    // Genera el comprobante en el formato concreto y retorna su representacion
    public abstract String generar();

    public abstract String getFormato();
}
