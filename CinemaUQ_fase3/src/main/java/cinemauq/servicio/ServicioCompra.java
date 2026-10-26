package cinemauq.servicio;
import cinemauq.excepcion.*;
import cinemauq.modelo.*;
import java.util.*;
/**
 * PATRÓN FACADE: un solo punto de entrada que coordina disponibilidad, pago, puntos, estado y registro.
 * Operación coordinada: si algo falla, los asientos se liberan y la compra queda PENDIENTE sin cobro.
 */
public class ServicioCompra {
    private final RepositorioCompras repo; private final List<ObservadorCompra> observadores = new ArrayList<>();
    public ServicioCompra(RepositorioCompras repo) { this.repo = repo; }
    public void suscribir(ObservadorCompra o) { observadores.add(o); }
    void notificar(String ev, Compra c) { observadores.forEach(o -> o.alOcurrir(ev, c)); }

    public void pagar(Compra compra, Cliente actor) {
        if (compra.cliente() != actor) throw new AccesoDenegadoException("La compra no pertenece al cliente");
        if (compra.estado() != Estados.PENDIENTE) throw new EstadoInvalidoException("La compra ya está " + compra.estado().nombre());
        Map<Funcion, List<Asiento>> reservados = new LinkedHashMap<>();
        try {
            for (var e : compra.asientosPorFuncion().entrySet()) { e.getKey().reservar(e.getValue()); reservados.put(e.getKey(), e.getValue()); }
            var hoy = ConfiguracionCine.getInstance().ahora().toLocalDate();
            if (compra.conPuntos()) {
                compra.guardarConsumos(actor.puntos().redimir(compra.costoPuntos(), hoy));
            } else {
                actor.tarjeta().cobrar(actor.id(), "PAGO-" + compra.id(), compra.total(), compra.id());
            }
        } catch (RuntimeException ex) {
            reservados.forEach(Funcion::liberar);   // nada queda reservado ni cobrado
            throw ex;
        }
        compra.pagar();
        if (!compra.conPuntos())   // regla del grupo: compras con puntos NO acumulan puntos
            actor.puntos().acumular((int) (compra.total() / ConfiguracionCine.COP_POR_PUNTO), compra.id(), ConfiguracionCine.getInstance().ahora().toLocalDate());
        repo.guardar(compra);
        notificar("COMPRA_PAGADA", compra);
    }
}
