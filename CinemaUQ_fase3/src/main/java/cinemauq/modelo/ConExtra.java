package cinemauq.modelo;
public class ConExtra extends DecoradorItem {
    private final String extra; private final long recargo;
    public ConExtra(ItemMenu base, String extra, long recargo) {
        super(base);
        if (recargo <= 0) throw new cinemauq.excepcion.ValidacionException("El recargo del complemento debe ser > 0");
        this.extra = extra; this.recargo = recargo;
    }
    @Override public String nombre() { return base.nombre() + " + " + extra; }
    @Override public long precio() { return base.precio() + recargo; }
}
