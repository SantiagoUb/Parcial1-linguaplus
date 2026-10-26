package cinemauq;
import cinemauq.modelo.*;
import cinemauq.reporte.*;
import java.nio.file.*;
import java.time.LocalDate;

public class MainFase2 {
    public static void main(String[] a) throws Exception {
        Path dir = Files.createTempDirectory("cinemauq"); Path db = dir.resolve("cinemauq.dat");
        var ctx = ContextoCine.iniciar(db); var admin = ctx.datos.admins.get(0); var cliente = ctx.datos.clientes.get(0);
        var desde = LocalDate.now().minusDays(90); var hasta = LocalDate.now().plusDays(10);
        System.out.println("Compras demo: " + ctx.datos.compras.todas().size() + " | funciones: " + ctx.datos.funciones.size());
        try { ctx.reportes.generar(cliente, TipoReporte.VENTAS_PERIODO, ctx.datos, desde, hasta); } catch (Exception e) { System.out.println("Cliente pidiendo reporte -> " + e.getClass().getSimpleName()); }
        for (var t : TipoReporte.values()) {
            var r = ctx.reportes.generar(admin, t, ctx.datos, desde, hasta);
            ctx.reportes.exportar(admin, r, new GeneradorTXT(), dir.resolve(t.name() + ".txt")); ctx.reportes.exportar(admin, r, new AdaptadorPDF(), dir.resolve(t.name() + ".pdf"));
            System.out.printf("%-18s filas=%-3d serie=%-3d %s%n", t.name(), r.filas.size(), r.serie.size(), r.totales);
        }
        System.out.println("\n" + ctx.estadisticas.calcular(admin, ctx.datos));
        ctx.recargar(admin, cliente, 50000); long saldo = cliente.tarjeta().saldo(); int n = ctx.datos.compras.todas().size();
        var otro = ContextoCine.iniciar(db);   // reinicio: se recarga desde disco
        var c2 = otro.datos.clientes.get(0);
        System.out.println("\nPersistencia -> saldo igual: " + (c2.tarjeta().saldo() == saldo) + " | compras iguales: " + (otro.datos.compras.todas().size() == n)
            + " | identidad de estado: " + otro.datos.compras.todas().stream().allMatch(c -> c.estado() instanceof Estados));
        System.out.println("Puntos Ana (lotes): "); c2.puntos().lotes().stream().limit(4).forEach(l -> System.out.println("  " + l));
        System.out.println("Archivos en " + dir);
        Files.copy(dir.resolve("VENTAS_PERIODO.pdf"), Path.of("/tmp/ventas.pdf"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(dir.resolve("VENTAS_PERIODO.txt"), Path.of("/tmp/ventas.txt"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(dir.resolve("CANCELACIONES.pdf"), Path.of("/tmp/canc.pdf"), StandardCopyOption.REPLACE_EXISTING);
    }
}
