package cinemauq.modelo;
import java.util.*;
public class Sala implements java.io.Serializable {
    private final String id; private String nombre; private int filas, columnas; private final List<Asiento> asientos = new ArrayList<>();
    public Sala(String nombre, int filas, int columnas) {
        this.id = Ids.nuevo("SAL"); this.nombre = nombre; reconstruir(filas, columnas);
    }
    /** Redistribuye los asientos. El servicio de catálogo solo lo permite si la sala no tiene funciones. */
    public void reconstruir(int filas, int columnas) {
        if (filas <= 0 || columnas <= 0 || filas > 26) throw new cinemauq.excepcion.ValidacionException("Sala: filas (1-26) y columnas > 0");
        this.filas = filas; this.columnas = columnas; asientos.clear();
        for (int f = 0; f < filas; f++) for (int c = 1; c <= columnas; c++) asientos.add(new Asiento((char) ('A' + f), c));
    }
    public void renombrar(String n) { if (n == null || n.isBlank()) throw new cinemauq.excepcion.ValidacionException("Nombre de sala obligatorio"); this.nombre = n; }
    public String id() { return id; } public String nombre() { return nombre; } public int filas() { return filas; } public int columnas() { return columnas; }
    public List<Asiento> asientos() { return Collections.unmodifiableList(asientos); }
    public int capacidad() { return asientos.size(); }
}
