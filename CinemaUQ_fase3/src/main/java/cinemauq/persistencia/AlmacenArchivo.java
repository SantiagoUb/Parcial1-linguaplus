package cinemauq.persistencia;
import java.io.*;
import java.nio.file.*;
/** Persistencia por serialización Java. Escritura atómica: archivo temporal + reemplazo (no queda a medias). */
public final class AlmacenArchivo {
    private AlmacenArchivo() {}
    public static synchronized void guardar(DatosCine datos, Path destino) {
        try {
            Path tmp = destino.resolveSibling(destino.getFileName() + ".tmp");
            try (var o = new ObjectOutputStream(Files.newOutputStream(tmp))) { o.writeObject(datos); }
            try { Files.move(tmp, destino, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
            catch (AtomicMoveNotSupportedException e) { Files.move(tmp, destino, StandardCopyOption.REPLACE_EXISTING); }
        } catch (IOException e) { throw new UncheckedIOException("No se pudo guardar", e); }
    }
    public static synchronized DatosCine cargar(Path origen) {
        try (var i = new ObjectInputStream(Files.newInputStream(origen))) { return (DatosCine) i.readObject(); }
        catch (IOException | ClassNotFoundException e) { throw new RuntimeException("No se pudo cargar " + origen, e); }
    }
}
