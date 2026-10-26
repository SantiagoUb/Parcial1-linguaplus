package cinemauq.modelo;
import java.time.LocalDateTime;
public record Movimiento(String id, TipoMovimiento tipo, long monto, LocalDateTime fecha, String referencia, long saldoPosterior) implements java.io.Serializable {}
