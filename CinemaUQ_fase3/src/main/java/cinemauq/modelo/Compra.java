package cinemauq.modelo;
import cinemauq.excepcion.ValidacionException;
import java.time.LocalDateTime;
import java.util.*;
public class Compra implements java.io.Serializable {
    private final String id = Ids.nuevo("CMP"); private final Cliente cliente;
    private final List<Entrada> entradas; private final List<LineaMenu> lineas; private final Promocion promocion;
    private final long subtotal, descuento, total; private final boolean conPuntos; private final int costoPuntos;
    private final LocalDateTime creada = ConfiguracionCine.getInstance().ahora();
    private LocalDateTime reembolsadaEn; private EstadoCompra estado = Estados.PENDIENTE; private long montoReembolsado;
    private List<CuentaPuntos.Consumo> consumos = List.of();

    private Compra(Builder b) {
        cliente = b.cliente; entradas = List.copyOf(b.entradas); lineas = List.copyOf(b.lineas); promocion = b.promocion; conPuntos = b.conPuntos;
        long sub = entradas.stream().mapToLong(e -> e.funcion().precioEntrada()).sum() + lineas.stream().mapToLong(LineaMenu::subtotal).sum();
        subtotal = sub; descuento = promocion == null ? 0 : sub * promocion.porcentaje() / 100; total = subtotal - descuento;
        costoPuntos = conPuntos ? (int) Math.ceil((double) total / ConfiguracionCine.VALOR_PUNTO_REDENCION) : 0;
    }
    public static Builder builder(Cliente c) { return new Builder(c); }

    /** PATRÓN BUILDER: valida obligatorios y reglas antes de crear una compra válida. */
    public static class Builder {
        private final Cliente cliente; private final List<Entrada> entradas = new ArrayList<>();
        private final List<LineaMenu> lineas = new ArrayList<>(); private Promocion promocion; private boolean conPuntos;
        private Builder(Cliente c) { if (c == null) throw new ValidacionException("Cliente obligatorio"); cliente = c; }
        public Builder entrada(Funcion f, Asiento a) {
            boolean repetida = entradas.stream().anyMatch(e -> e.funcion() == f && e.asiento().equals(a));
            if (repetida) throw new ValidacionException("Asiento " + a + " repetido en la compra");
            entradas.add(new Entrada(Ids.nuevo("ENT"), f, a)); return this;
        }
        public Builder item(ItemMenu i, int cantidad) {
            if (cantidad <= 0) throw new ValidacionException("Cantidad inválida"); lineas.add(new LineaMenu(i, cantidad)); return this;
        }
        public Builder promocion(Promocion p) { this.promocion = p; return this; }
        public Builder pagarConPuntos() { this.conPuntos = true; return this; }
        public Compra build() {
            if (entradas.isEmpty() && lineas.isEmpty()) throw new ValidacionException("La compra no tiene elementos");
            if (promocion != null) {
                if (conPuntos) throw new ValidacionException("Las promociones no aplican a pagos con puntos");
                if (!promocion.vigente(ConfiguracionCine.getInstance().ahora().toLocalDate())) throw new ValidacionException("Promoción vencida");
            }
            return new Compra(this);
        }
    }
    public void pagar() { estado.pagar(this); }
    public void reembolsar(long monto) { estado.reembolsar(this, monto); }
    void setEstado(EstadoCompra e) { this.estado = e; }
    void registrarReembolso(long m) { this.montoReembolsado = m; this.reembolsadaEn = ConfiguracionCine.getInstance().ahora(); }
    public LocalDateTime reembolsadaEn() { return reembolsadaEn; }
    public Optional<LocalDateTime> inicioMasTemprano() { return entradas.stream().map(e -> e.funcion().inicio()).min(Comparator.naturalOrder()); }
    public boolean hayFuncionCancelada() { return entradas.stream().anyMatch(e -> e.funcion().cancelada()); }
    public boolean contiene(Funcion f) { return entradas.stream().anyMatch(e -> e.funcion() == f); }
    public Map<Funcion, List<Asiento>> asientosPorFuncion() {
        Map<Funcion, List<Asiento>> m = new LinkedHashMap<>();
        for (Entrada e : entradas) m.computeIfAbsent(e.funcion(), k -> new ArrayList<>()).add(e.asiento());
        return m;
    }
    public void guardarConsumos(List<CuentaPuntos.Consumo> c) { this.consumos = c; }
    public List<CuentaPuntos.Consumo> consumos() { return consumos; }
    public String id() { return id; } public Cliente cliente() { return cliente; } public EstadoCompra estado() { return estado; }
    public List<Entrada> entradas() { return entradas; } public List<LineaMenu> lineas() { return lineas; }
    public long subtotal() { return subtotal; } public long descuento() { return descuento; } public long total() { return total; }
    public boolean conPuntos() { return conPuntos; } public int costoPuntos() { return costoPuntos; }
    public long montoReembolsado() { return montoReembolsado; } public LocalDateTime creada() { return creada; }
}
