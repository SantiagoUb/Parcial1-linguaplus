package cinemauq.reporte;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
public class GeneradorTXT implements GeneradorReporte {
    public String extension() { return "txt"; }
    public void exportar(Reporte r, Path destino) throws IOException { Files.write(destino, r.comoTexto(), StandardCharsets.UTF_8); }
}
