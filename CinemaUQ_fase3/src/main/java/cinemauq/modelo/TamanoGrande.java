package cinemauq.modelo;
public class TamanoGrande extends DecoradorItem {
    public TamanoGrande(ItemMenu base) { super(base); }
    @Override public String nombre() { return base.nombre() + " (grande)"; }
    @Override public long precio() { return base.precio() * 125 / 100; }
}
