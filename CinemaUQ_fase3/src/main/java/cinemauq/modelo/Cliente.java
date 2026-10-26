package cinemauq.modelo;
public class Cliente extends Usuario {
    private final TarjetaVirtual tarjeta;
    private final CuentaPuntos puntos = new CuentaPuntos();
    public Cliente(String nombre, String email, String password) {
        super(Ids.nuevo("CLI"), nombre, email, password, Rol.CLIENTE);
        this.tarjeta = new TarjetaVirtual(Ids.nuevo("TJ"), id());
    }
    public TarjetaVirtual tarjeta() { return tarjeta; }
    public CuentaPuntos puntos() { return puntos; }
}
