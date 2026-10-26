package cinemauq.servicio;
import cinemauq.modelo.*;
public class ServicioTarjetas {
    public void recargar(Usuario admin, Cliente cliente, long monto) {
        Autorizacion.exigirAdmin(admin);
        cliente.tarjeta().recargar(Ids.nuevo("REC"), monto, "Recarga por " + admin.nombre());
    }
}
