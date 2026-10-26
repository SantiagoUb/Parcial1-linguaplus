package cinemauq.servicio;
import cinemauq.excepcion.*;
import cinemauq.modelo.*;
import java.util.*;
public class ServicioCancelacion {
    private final RepositorioCompras repo; private final ServicioCompra compras;
    private final List<PoliticaReembolso> politicas = List.of(new PoliticaFuncionCancelada(), new PoliticaOrdinaria());
    public ServicioCancelacion(RepositorioCompras repo, ServicioCompra compras) { this.repo = repo; this.compras = compras; }

    public void cancelarCompra(Compra c, Usuario actor) {
        if (actor == null || (actor != c.cliente() && actor.rol() != Rol.ADMINISTRADOR)) throw new AccesoDenegadoException("Sin permiso sobre esta compra");
        procesar(c);
    }
    /** Decisión: si se cancela una función, TODA compra que la incluya se cancela y reembolsa al 100 %. */
    public int cancelarFuncion(Usuario admin, Funcion f) {
        Autorizacion.exigirAdmin(admin);
        f.cancelar(); int n = 0;
        for (Compra c : repo.todas()) if (c.contiene(f) && c.estado() == Estados.PAGADA) { procesar(c); n++; }
        return n;
    }
    private void procesar(Compra c) {
        if (c.estado() != Estados.PAGADA) throw new EstadoInvalidoException("Solo se cancelan compras PAGADAS (estado: " + c.estado().nombre() + ")");
        var ahora = ConfiguracionCine.getInstance().ahora();
        int pct = politicas.stream().filter(p -> p.aplica(c)).findFirst().orElseThrow().porcentaje(c, ahora); // puede lanzar
        long monto = c.conPuntos() ? 0 : c.total() * pct / 100;
        if (c.conPuntos()) c.cliente().puntos().restaurar(c.consumos(), pct);
        else c.cliente().tarjeta().reembolsar("REF-" + c.id(), monto, c.id());   // una sola vez por operación
        c.asientosPorFuncion().forEach(Funcion::liberar);
        c.cliente().puntos().revocarPorCompra(c.id());   // decisión: se anulan los puntos ganados por la compra
        c.reembolsar(monto);
        compras.notificar("COMPRA_REEMBOLSADA", c);
    }
}
