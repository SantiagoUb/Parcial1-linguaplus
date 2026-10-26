package cinemauq.servicio;
import cinemauq.modelo.Compra;
import java.util.*;
public class RepositorioCompras implements java.io.Serializable {
    private final List<Compra> compras = new ArrayList<>();
    public synchronized void guardar(Compra c) { if (!compras.contains(c)) compras.add(c); }
    public synchronized List<Compra> todas() { return List.copyOf(compras); }
}
