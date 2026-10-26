package cinemauq.servicio;
import cinemauq.excepcion.ValidacionException;
import cinemauq.modelo.*;
import cinemauq.persistencia.DatosCine;
import java.util.stream.Stream;
public class ServicioUsuarios {
    private final DatosCine d; private final Runnable persistir;
    public ServicioUsuarios(DatosCine d, Runnable persistir) { this.d = d; this.persistir = persistir; }
    private Stream<Usuario> todos() { return Stream.concat(d.admins.stream(), d.clientes.stream()); }
    /** RF-001. Email único; el Cliente nace con su tarjeta virtual y su cuenta de puntos. */
    public synchronized Cliente registrar(String nombre, String email, String password) {
        if (email != null && todos().anyMatch(u -> u.email().equalsIgnoreCase(email.trim()))) throw new ValidacionException("Ya existe una cuenta con ese correo");
        var c = new Cliente(nombre == null ? null : nombre.trim(), email == null ? null : email.trim(), password);
        d.clientes.add(c); persistir.run(); return c;
    }
    /** RF-002. */
    public Usuario autenticar(String email, String password) {
        return todos().filter(u -> u.autenticar(email == null ? "" : email.trim(), password == null ? "" : password)).findFirst()
                .orElseThrow(() -> new ValidacionException("Correo o contraseña incorrectos"));
    }
}
