package cinemauq.servicio;
import cinemauq.excepcion.*;
import cinemauq.modelo.*;
import cinemauq.persistencia.DatosCine;
import java.time.*;
import java.util.*;
/** RF-017..023. Todas las operaciones exigen administrador y respetan las restricciones sobre elementos con compras. */
public class ServicioCatalogo {
    private static final int LIMPIEZA_MIN = 15;
    private final DatosCine d; private final Runnable persistir;
    public ServicioCatalogo(DatosCine d, Runnable persistir) { this.d = d; this.persistir = persistir; }

    private boolean tieneCompras(Funcion f) { return d.compras.todas().stream().anyMatch(c -> c.contiene(f)); }
    private List<Funcion> funcionesDe(Pelicula p) { return d.funciones.stream().filter(f -> f.pelicula().id().equals(p.id())).toList(); }
    private List<Funcion> funcionesDe(Sala s) { return d.funciones.stream().filter(f -> f.sala() == s).toList(); }
    private void noVacio(String v, String campo) { if (v == null || v.isBlank()) throw new ValidacionException(campo + " obligatorio"); }
    private <T> T buscar(List<T> l, java.util.function.Predicate<T> p, String que) { return l.stream().filter(p).findFirst().orElseThrow(() -> new ValidacionException(que + " no encontrado")); }
    public Pelicula pelicula(String id) { return buscar(d.peliculas, p -> p.id().equals(id), "Película"); }
    public Sala sala(String id) { return buscar(d.salas, s -> s.id().equals(id), "Sala"); }

    // ---------- Películas ----------
    public Pelicula crearPelicula(Usuario a, String titulo, String genero, int dur, String clasif) {
        Autorizacion.exigirAdmin(a); noVacio(titulo, "Título");
        if (d.peliculas.stream().anyMatch(p -> p.titulo().equalsIgnoreCase(titulo.trim()))) throw new ValidacionException("Ya existe una película con ese título");
        var p = new Pelicula(Ids.nuevo("PEL"), titulo.trim(), genero, dur, clasif); d.peliculas.add(p); persistir.run(); return p;
    }
    public void actualizarPelicula(Usuario a, String id, String titulo, String genero, int dur, String clasif) {
        Autorizacion.exigirAdmin(a); var vieja = pelicula(id);
        if (funcionesDe(vieja).stream().anyMatch(this::tieneCompras)) throw new RestriccionCatalogoException("La película tiene funciones con compras: no se puede modificar");
        var nueva = new Pelicula(id, titulo, genero, dur, clasif);
        d.peliculas.set(d.peliculas.indexOf(vieja), nueva); funcionesDe(vieja).forEach(f -> f.reasignarPelicula(nueva)); persistir.run();
    }
    public void eliminarPelicula(Usuario a, String id) {
        Autorizacion.exigirAdmin(a); var p = pelicula(id);
        if (!funcionesDe(p).isEmpty()) throw new RestriccionCatalogoException("La película tiene funciones programadas: elimínelas o cancélelas primero");
        d.peliculas.remove(p); persistir.run();
    }
    // ---------- Salas y asientos ----------
    public Sala crearSala(Usuario a, String nombre, int filas, int cols) {
        Autorizacion.exigirAdmin(a); noVacio(nombre, "Nombre");
        if (d.salas.stream().anyMatch(s -> s.nombre().equalsIgnoreCase(nombre.trim()))) throw new ValidacionException("Ya existe una sala con ese nombre");
        var s = new Sala(nombre.trim(), filas, cols); d.salas.add(s); persistir.run(); return s;
    }
    public void actualizarSala(Usuario a, String id, String nombre, int filas, int cols) {
        Autorizacion.exigirAdmin(a); var s = sala(id);
        boolean cambiaDistribucion = filas != s.filas() || cols != s.columnas();
        if (cambiaDistribucion && !funcionesDe(s).isEmpty()) throw new RestriccionCatalogoException("La sala tiene funciones: solo puede renombrarse, no cambiar sus asientos");
        s.renombrar(nombre); if (cambiaDistribucion) s.reconstruir(filas, cols); persistir.run();
    }
    public void eliminarSala(Usuario a, String id) {
        Autorizacion.exigirAdmin(a); var s = sala(id);
        if (!funcionesDe(s).isEmpty()) throw new RestriccionCatalogoException("La sala tiene funciones asociadas");
        d.salas.remove(s); persistir.run();
    }
    // ---------- Funciones ----------
    private void validarHorario(Sala s, Pelicula p, LocalDateTime ini, Funcion ignorar) {
        var fin = ini.plusMinutes(p.duracionMin() + LIMPIEZA_MIN);
        for (var g : funcionesDe(s)) {
            if (g == ignorar || g.cancelada()) continue;
            var gFin = g.inicio().plusMinutes(g.pelicula().duracionMin() + LIMPIEZA_MIN);
            if (ini.isBefore(gFin) && g.inicio().isBefore(fin)) throw new ValidacionException("La sala " + s.nombre() + " está ocupada en ese horario por «" + g.pelicula().titulo() + "»");
        }
    }
    public Funcion programarFuncion(Usuario a, String peliculaId, String salaId, LocalDateTime inicio, long precio) {
        Autorizacion.exigirAdmin(a); var p = pelicula(peliculaId); var s = sala(salaId);
        if (inicio == null || !inicio.isAfter(ConfiguracionCine.getInstance().ahora())) throw new ValidacionException("La función debe programarse a futuro");
        validarHorario(s, p, inicio, null);
        var f = new Funcion(p, s, inicio, precio); d.funciones.add(f); persistir.run(); return f;
    }
    /** Solo funciones sin compras; se reemplaza por una nueva con los datos corregidos. */
    public Funcion reprogramarFuncion(Usuario a, Funcion vieja, String salaId, LocalDateTime inicio, long precio) {
        Autorizacion.exigirAdmin(a);
        if (tieneCompras(vieja)) throw new RestriccionCatalogoException("La función tiene compras: no se puede modificar (puede cancelarla)");
        var s = sala(salaId);
        if (inicio == null || !inicio.isAfter(ConfiguracionCine.getInstance().ahora())) throw new ValidacionException("La función debe programarse a futuro");
        validarHorario(s, vieja.pelicula(), inicio, vieja);
        var nueva = new Funcion(vieja.pelicula(), s, inicio, precio); d.funciones.set(d.funciones.indexOf(vieja), nueva); persistir.run(); return nueva;
    }
    public void eliminarFuncion(Usuario a, Funcion f) {
        Autorizacion.exigirAdmin(a);
        if (tieneCompras(f)) throw new RestriccionCatalogoException("La función tiene compras: use «Cancelar función» para reembolsar");
        d.funciones.remove(f); persistir.run();
    }
    // ---------- Productos, combos y promociones ----------
    private boolean enCombo(Producto p) { return d.combos.stream().anyMatch(c -> c.contiene(p)); }
    public Producto crearProducto(Usuario a, String nombre, long precio) {
        Autorizacion.exigirAdmin(a); noVacio(nombre, "Nombre");
        var p = new Producto(Ids.nuevo("PRD"), nombre.trim(), precio); d.productos.add(p); persistir.run(); return p;
    }
    public void actualizarProducto(Usuario a, Producto viejo, String nombre, long precio) {
        Autorizacion.exigirAdmin(a);
        if (enCombo(viejo)) throw new RestriccionCatalogoException("El producto forma parte de un combo: elimine el combo primero");
        d.productos.set(d.productos.indexOf(viejo), new Producto(viejo.id(), nombre, precio)); persistir.run();   // el historial conserva el precio antiguo
    }
    public void eliminarProducto(Usuario a, Producto p) {
        Autorizacion.exigirAdmin(a);
        if (enCombo(p)) throw new RestriccionCatalogoException("El producto forma parte de un combo");
        d.productos.remove(p); persistir.run();
    }
    public Combo crearCombo(Usuario a, String nombre, int descuento, List<Producto> productos) {
        Autorizacion.exigirAdmin(a); noVacio(nombre, "Nombre");
        if (productos == null || productos.size() < 2) throw new ValidacionException("Un combo requiere al menos 2 productos");
        var c = new Combo(nombre.trim(), descuento); productos.forEach(c::agregar); d.combos.add(c); persistir.run(); return c;
    }
    public void eliminarCombo(Usuario a, Combo c) { Autorizacion.exigirAdmin(a); d.combos.remove(c); persistir.run(); }
    public Promocion crearPromocion(Usuario a, String codigo, String nombre, int pct, LocalDate desde, LocalDate hasta) {
        Autorizacion.exigirAdmin(a); noVacio(codigo, "Código");
        if (pct < 1 || pct > 100) throw new ValidacionException("El porcentaje debe estar entre 1 y 100");
        if (desde == null || hasta == null || hasta.isBefore(desde)) throw new ValidacionException("Rango de vigencia inválido");
        if (d.promociones.stream().anyMatch(p -> p.codigo().equalsIgnoreCase(codigo.trim()))) throw new ValidacionException("Ya existe una promoción con ese código");
        var p = new Promocion(codigo.trim().toUpperCase(), nombre, pct, desde, hasta); d.promociones.add(p); persistir.run(); return p;
    }
    public void eliminarPromocion(Usuario a, Promocion p) { Autorizacion.exigirAdmin(a); d.promociones.remove(p); persistir.run(); }
}
