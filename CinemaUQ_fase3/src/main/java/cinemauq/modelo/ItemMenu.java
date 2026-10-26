package cinemauq.modelo;
/** PATRÓN COMPOSITE (componente): producto individual y combo se tratan igual. */
public interface ItemMenu extends java.io.Serializable {
    default String nombreBase() { return nombre(); }
    String nombre(); long precio(); String describir(String sangria);
}
