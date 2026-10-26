package cinemauq.reporte;
import cinemauq.modelo.*;
import cinemauq.persistencia.DatosCine;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
public class ServicioReportes {
    public Reporte generar(Usuario admin, TipoReporte tipo, DatosCine d, LocalDate desde, LocalDate hasta) {
        Autorizacion.exigirAdmin(admin);
        if (desde == null || hasta == null || hasta.isBefore(desde)) throw new cinemauq.excepcion.ValidacionException("Rango de fechas inválido");
        return FabricaReportes.crear(tipo).generar(d, desde, hasta);
    }
    public void exportar(Usuario admin, Reporte r, GeneradorReporte g, Path destino) throws IOException { Autorizacion.exigirAdmin(admin); g.exportar(r, destino); }
}
