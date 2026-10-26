package cinemauq.modelo;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
public abstract class Usuario implements java.io.Serializable {
    private final String id, nombre, email, hash;
    private final Rol rol;
    protected Usuario(String id, String nombre, String email, String password, Rol rol) {
        if (nombre == null || nombre.isBlank() || email == null || !email.contains("@") || password == null || password.length() < 6)
            throw new cinemauq.excepcion.ValidacionException("Nombre, email válido y contraseña (mín. 6) son obligatorios");
        this.id = id; this.nombre = nombre; this.email = email; this.rol = rol; this.hash = hash(password);
    }
    private static String hash(String p) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(p.getBytes(StandardCharsets.UTF_8))); }
        catch (Exception e) { throw new IllegalStateException(e); }
    }
    public boolean autenticar(String email, String password) { return this.email.equalsIgnoreCase(email) && hash.equals(hash(password)); }
    public String id() { return id; } public String nombre() { return nombre; }
    public String email() { return email; } public Rol rol() { return rol; }
}
