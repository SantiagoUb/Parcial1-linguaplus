package cinemauq.excepcion;
/** Se intentó modificar/eliminar algo que tiene compras o dependencias. */
public class RestriccionCatalogoException extends CinemaException {
    public RestriccionCatalogoException(String m) { super(m); }
}
