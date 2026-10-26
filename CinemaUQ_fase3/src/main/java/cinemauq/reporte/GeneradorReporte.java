package cinemauq.reporte;
import java.io.IOException;
import java.nio.file.Path;
/** Interfaz propia de CinemaUQ para exportar. La generación de documentos está separada de las consultas. */
public interface GeneradorReporte { String extension(); void exportar(Reporte reporte, Path destino) throws IOException; }
