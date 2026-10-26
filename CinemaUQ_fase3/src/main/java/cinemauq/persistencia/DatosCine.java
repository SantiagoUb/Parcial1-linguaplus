package cinemauq.persistencia;
import cinemauq.modelo.*;
import cinemauq.servicio.RepositorioCompras;
import java.io.Serializable;
import java.util.*;
/** Raíz del grafo de objetos: se guarda completo en UNA escritura para conservar referencias compartidas. */
public class DatosCine implements Serializable {
    public final List<Administrador> admins = new ArrayList<>();
    public final List<Cliente> clientes = new ArrayList<>();
    public final List<Pelicula> peliculas = new ArrayList<>();
    public final List<Sala> salas = new ArrayList<>();
    public final List<Funcion> funciones = new ArrayList<>();
    public final List<Producto> productos = new ArrayList<>();
    public final List<Combo> combos = new ArrayList<>();
    public final List<Promocion> promociones = new ArrayList<>();
    public final RepositorioCompras compras = new RepositorioCompras();
}
