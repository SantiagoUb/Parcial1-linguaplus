package cinemauq.modelo;
import cinemauq.excepcion.EstadoInvalidoException;
/** PATRÓN STATE: cada estado define qué transiciones son válidas. */
public interface EstadoCompra {
    String nombre();
    default void pagar(Compra c) { throw new EstadoInvalidoException("No se puede pagar una compra " + nombre()); }
    default void reembolsar(Compra c, long monto) { throw new EstadoInvalidoException("No se puede reembolsar una compra " + nombre()); }
}
