package cinemauq;
import cinemauq.modelo.*;
import cinemauq.persistencia.*;
import cinemauq.reporte.*;
import cinemauq.servicio.*;
import java.nio.file.*;
/** Compone servicios y persistencia. Cada compra/reembolso se guarda solo (Observer); las recargas guardan al terminar. */
public class ContextoCine {
    public final DatosCine datos; public final ServicioCompra compra; public final ServicioCancelacion cancelacion;
    public final ServicioTarjetas tarjetas = new ServicioTarjetas(); public final ServicioCatalogo catalogo; public final ServicioUsuarios usuarios; public final ServicioReportes reportes = new ServicioReportes();
    public final ServicioEstadisticas estadisticas = new ServicioEstadisticas(); private final Path archivo;
    private ContextoCine(DatosCine d, Path archivo) {
        this.datos = d; this.archivo = archivo; this.catalogo = new ServicioCatalogo(d, this::guardar); this.usuarios = new ServicioUsuarios(d, this::guardar); this.compra = new ServicioCompra(d.compras); this.cancelacion = new ServicioCancelacion(d.compras, compra);
        compra.suscribir((ev, c) -> guardar());
    }
    public static ContextoCine iniciar(Path archivo) {
        boolean nuevo = !Files.exists(archivo); var c = new ContextoCine(nuevo ? DatosDemo.crear() : AlmacenArchivo.cargar(archivo), archivo);
        if (nuevo) c.guardar(); return c;
    }
    public void guardar() { AlmacenArchivo.guardar(datos, archivo); }
    public void recargar(Usuario admin, Cliente cliente, long monto) { tarjetas.recargar(admin, cliente, monto); guardar(); }
}
