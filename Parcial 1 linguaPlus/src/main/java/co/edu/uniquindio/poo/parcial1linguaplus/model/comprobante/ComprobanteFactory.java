package co.edu.uniquindio.poo.parcial1linguaplus.model.comprobante;

import co.edu.uniquindio.poo.parcial1linguaplus.model.Matricula;

/*
 * Creator del Factory Method para comprobantes. Permite agregar nuevos formatos
 * (la academia ya evalua un tercer formato) sin modificar el codigo que los consume,
 * solo agregando una nueva subclase de Comprobante + ComprobanteFactory.
 */

public abstract class ComprobanteFactory {
    public abstract Comprobante generarComprobante(Matricula matricula);

    public String emitirComprobante(Matricula matricula) {
        Comprobante comprobante = generarComprobante(matricula);
        return comprobante.generar();
    }
}
