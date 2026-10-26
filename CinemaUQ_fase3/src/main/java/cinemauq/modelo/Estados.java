package cinemauq.modelo;
/** Estados concretos del patrón State. Son enum para conservar la identidad tras serializar. */
public enum Estados implements EstadoCompra {
    PENDIENTE {
        public String nombre() { return "PENDIENTE"; }
        public void pagar(Compra c) { c.setEstado(PAGADA); }
    },
    PAGADA {
        public String nombre() { return "PAGADA"; }
        public void reembolsar(Compra c, long monto) { c.registrarReembolso(monto); c.setEstado(REEMBOLSADA); }
    },
    /** Estado final: cancelada y reembolsada (un solo reembolso por compra). */
    REEMBOLSADA { public String nombre() { return "REEMBOLSADA"; } }
}
