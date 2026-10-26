package cinemauq;
import cinemauq.excepcion.CinemaException;
import java.nio.file.*;
import java.time.LocalDateTime;
/** Prueba de las reglas de gestión (sin UI). */
public class MainFase3 {
    static void t(String n, Runnable r) { try { r.run(); System.out.println("  OK        " + n); } catch (CinemaException e) { System.out.println("  RECHAZADO " + n + " -> " + e.getClass().getSimpleName() + ": " + e.getMessage()); } }
    public static void main(String[] x) throws Exception {
        var ctx = ContextoCine.iniciar(Files.createTempDirectory("cq3").resolve("db.dat")); var cat = ctx.catalogo; var adm = ctx.datos.admins.get(0); var cli = ctx.datos.clientes.get(0);
        t("Cliente crea película", () -> cat.crearPelicula(cli, "X", "G", 100, "TP"));
        t("Admin crea película", () -> cat.crearPelicula(adm, "Nueva", "Drama", 100, "TP"));
        t("Título duplicado", () -> cat.crearPelicula(adm, "nueva", "Drama", 100, "TP"));
        var conCompras = ctx.datos.funciones.stream().filter(f -> ctx.datos.compras.todas().stream().anyMatch(c -> c.contiene(f))).findFirst().get();
        t("Modificar película con compras", () -> cat.actualizarPelicula(adm, conCompras.pelicula().id(), "Otro", "G", 90, "TP"));
        t("Eliminar película con funciones", () -> cat.eliminarPelicula(adm, conCompras.pelicula().id()));
        t("Eliminar función con compras", () -> cat.eliminarFuncion(adm, conCompras));
        t("Redimensionar sala con funciones", () -> cat.actualizarSala(adm, conCompras.sala().id(), "Sala", 2, 2));
        var sala = cat.crearSala(adm, "Sala 4", 3, 5); var peli = ctx.datos.peliculas.get(0); var ini = LocalDateTime.now().plusDays(10).withHour(18).withMinute(0).withSecond(0).withNano(0);
        t("Programar función", () -> cat.programarFuncion(adm, peli.id(), sala.id(), ini, 15000));
        t("Solapamiento en la misma sala", () -> cat.programarFuncion(adm, peli.id(), sala.id(), ini.plusMinutes(60), 15000));
        t("Función en el pasado", () -> cat.programarFuncion(adm, peli.id(), sala.id(), LocalDateTime.now().minusDays(1), 15000));
        t("Eliminar producto de un combo", () -> cat.eliminarProducto(adm, ctx.datos.combos.get(0).componentes().stream().map(i -> (cinemauq.modelo.Producto) i).findFirst().get()));
        t("Registrar cliente nuevo", () -> ctx.usuarios.registrar("Nuevo", "nuevo@x.co", "abc12345"));
        t("Registrar correo repetido", () -> ctx.usuarios.registrar("Otro", "NUEVO@x.co", "abc12345"));
        t("Login incorrecto", () -> ctx.usuarios.autenticar("nuevo@x.co", "mala"));
        t("Login correcto", () -> ctx.usuarios.autenticar("nuevo@x.co", "abc12345"));
    }
}
