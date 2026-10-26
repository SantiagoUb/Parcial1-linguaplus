package cinemauq.modelo;
import cinemauq.excepcion.*;
import java.util.*;
/** Operaciones atómicas e idempotentes por operacionId (nunca se procesa dos veces). */
public class TarjetaVirtual implements java.io.Serializable {
    private final String id, clienteId; private boolean activa = true; private long saldo;
    private final List<Movimiento> movimientos = new ArrayList<>(); private final Set<String> operaciones = new HashSet<>();
    public TarjetaVirtual(String id, String clienteId) { this.id = id; this.clienteId = clienteId; }
    public synchronized void cobrar(String clienteId, String opId, long monto, String ref) {
        if (!this.clienteId.equals(clienteId)) throw new AccesoDenegadoException("La tarjeta no pertenece al cliente");
        if (!activa) throw new TarjetaInactivaException("Tarjeta inactiva");
        if (operaciones.contains(opId)) throw new PagoDuplicadoException("Operación ya procesada: " + opId);
        if (saldo < monto) throw new SaldoInsuficienteException("Saldo insuficiente: " + saldo + " < " + monto);
        saldo -= monto; operaciones.add(opId); registrar(TipoMovimiento.PAGO, monto, ref);
    }
    public synchronized void recargar(String opId, long monto, String ref) {
        if (monto <= 0) throw new ValidacionException("Monto de recarga inválido");
        if (!operaciones.add(opId)) throw new PagoDuplicadoException("Recarga ya procesada");
        saldo += monto; registrar(TipoMovimiento.RECARGA, monto, ref);
    }
    public synchronized void reembolsar(String opId, long monto, String ref) {
        if (!operaciones.add(opId)) throw new ReembolsoYaEjecutadoException("El reembolso ya fue ejecutado: " + opId);
        saldo += monto; registrar(TipoMovimiento.REEMBOLSO, monto, ref);
    }
    private void registrar(TipoMovimiento t, long m, String ref) {
        movimientos.add(new Movimiento(Ids.nuevo("MOV"), t, m, ConfiguracionCine.getInstance().ahora(), ref, saldo));
    }
    public void desactivar() { activa = false; } public synchronized boolean activa() { return activa; }
    public synchronized long saldo() { return saldo; }
    public synchronized List<Movimiento> movimientos() { return List.copyOf(movimientos); }
    public String id() { return id; }
}
