package cinemauq.modelo;
import cinemauq.excepcion.*;
import java.time.LocalDateTime;
import java.util.*;
/** Disponibilidad = combinación (función, asiento). Reserva atómica y sincronizada. */
public class Funcion implements java.io.Serializable {
    private final String id; private Pelicula pelicula; private final Sala sala;
    private final LocalDateTime inicio; private final long precioEntrada;
    private boolean cancelada;
    private final Set<Asiento> ocupados = new HashSet<>();
    public Funcion(Pelicula p, Sala s, LocalDateTime inicio, long precioEntrada) {
        if (p == null || s == null || inicio == null || precioEntrada <= 0) throw new ValidacionException("Función: película, sala, fecha/hora y precio obligatorios");
        this.id = Ids.nuevo("FUN"); this.pelicula = p; this.sala = s; this.inicio = inicio; this.precioEntrada = precioEntrada;
    }
    /** Todo o nada: si algún asiento está ocupado no se reserva ninguno. */
    public synchronized void reservar(Collection<Asiento> pedidos) {
        if (cancelada) throw new FuncionNoDisponibleException("La función fue cancelada");
        if (!inicio.isAfter(ConfiguracionCine.getInstance().ahora())) throw new FuncionNoDisponibleException("La función ya inició");
        for (Asiento a : pedidos) {
            if (!sala.asientos().contains(a)) throw new ValidacionException("El asiento " + a + " no pertenece a la sala");
            if (ocupados.contains(a)) throw new AsientoNoDisponibleException("Asiento " + a + " ya vendido para esta función");
        }
        ocupados.addAll(pedidos);
    }
    public synchronized void liberar(Collection<Asiento> a) { ocupados.removeAll(a); }
    public synchronized boolean estaOcupado(Asiento a) { return ocupados.contains(a); }
    public synchronized int ocupados() { return ocupados.size(); }
    public synchronized void reasignarPelicula(Pelicula p) { this.pelicula = p; }
    public synchronized void cancelar() { cancelada = true; }
    public boolean cancelada() { return cancelada; }
    public String id() { return id; } public Pelicula pelicula() { return pelicula; } public Sala sala() { return sala; }
    public LocalDateTime inicio() { return inicio; } public long precioEntrada() { return precioEntrada; }
}
