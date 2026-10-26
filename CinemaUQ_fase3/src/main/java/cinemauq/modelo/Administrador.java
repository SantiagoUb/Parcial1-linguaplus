package cinemauq.modelo;
public class Administrador extends Usuario {
    public Administrador(String nombre, String email, String password) {
        super(Ids.nuevo("ADM"), nombre, email, password, Rol.ADMINISTRADOR);
    }
}
