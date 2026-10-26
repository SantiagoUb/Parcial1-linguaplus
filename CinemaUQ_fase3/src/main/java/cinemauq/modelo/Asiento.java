package cinemauq.modelo;
public record Asiento(char fila, int numero) implements java.io.Serializable {
    public String codigo() { return "" + fila + numero; }
    @Override public String toString() { return codigo(); }
}
