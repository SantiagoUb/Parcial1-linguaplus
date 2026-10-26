package cinemauq.reporte;
import java.io.IOException;
import java.nio.file.*;
/** PATRÓN ADAPTER: adapta LibreriaPdfLegada a la interfaz GeneradorReporte que usa el sistema. */
public class AdaptadorPDF implements GeneradorReporte {
    public String extension() { return "pdf"; }
    public void exportar(Reporte r, Path destino) throws IOException {
        var lib = new LibreriaPdfLegada(); r.comoTexto().forEach(lib::escribirLinea); Files.write(destino, lib.renderizar());
    }
}
