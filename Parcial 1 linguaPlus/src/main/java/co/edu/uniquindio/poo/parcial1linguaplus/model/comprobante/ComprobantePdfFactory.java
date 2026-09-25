package co.edu.uniquindio.poo.parcial1linguaplus.model.comprobante;

import co.edu.uniquindio.poo.parcial1linguaplus.model.Matricula;

public class ComprobantePdfFactory extends ComprobanteFactory {

    @Override
    public Comprobante generarComprobante(Matricula matricula) {
        return new ComprobantePdf(matricula);
    }
}
