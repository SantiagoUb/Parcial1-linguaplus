package cinemauq.excepcion;
/** Raíz de todos los errores de negocio de CinemaUQ. */
public class CinemaException extends RuntimeException {
    public CinemaException(String m) { super(m); }
}
