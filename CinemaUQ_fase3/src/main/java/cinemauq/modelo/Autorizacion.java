package cinemauq.modelo;
import cinemauq.excepcion.AccesoDenegadoException;
/** Validación de permisos EN LA LÓGICA (no solo ocultando botones). */
public final class Autorizacion {
    private Autorizacion() {}
    public static void exigirAdmin(Usuario u) {
        if (u == null || u.rol() != Rol.ADMINISTRADOR) throw new AccesoDenegadoException("Operación reservada al administrador");
    }
}
