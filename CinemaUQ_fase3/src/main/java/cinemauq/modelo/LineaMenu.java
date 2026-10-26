package cinemauq.modelo;
public record LineaMenu(ItemMenu item, int cantidad) implements java.io.Serializable { public long subtotal() { return item.precio() * cantidad; } }
