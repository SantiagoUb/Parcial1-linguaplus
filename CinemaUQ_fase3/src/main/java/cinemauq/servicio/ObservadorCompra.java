package cinemauq.servicio;
import cinemauq.modelo.Compra;
/** PATRÓN OBSERVER: la UI (o un log) se suscribe sin que el servicio la conozca. */
public interface ObservadorCompra { void alOcurrir(String evento, Compra compra); }
