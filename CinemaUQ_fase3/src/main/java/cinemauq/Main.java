package cinemauq;
import cinemauq.excepcion.CinemaException;
import cinemauq.modelo.*;
import cinemauq.servicio.*;
import java.time.*;
public class Main {
    static void intentar(String t, Runnable r) {
        try { r.run(); System.out.println("  OK    " + t); } catch (CinemaException e) { System.out.println("  RECHAZADO " + t + " -> " + e.getClass().getSimpleName() + ": " + e.getMessage()); }
    }
    public static void main(String[] args) {
        var repo = new RepositorioCompras(); var compras = new ServicioCompra(repo);
        var cancel = new ServicioCancelacion(repo, compras); var tarjetas = new ServicioTarjetas();
        compras.suscribir((ev, c) -> System.out.println("  [evento] " + ev + " " + c.id()));
        var admin = new Administrador("Admin", "admin@uq.co", "secreto1");
        var ana = new Cliente("Ana", "ana@uq.co", "clave123"); var luis = new Cliente("Luis", "luis@uq.co", "clave123");
        var ahora = ConfiguracionCine.getInstance().ahora();
        var funcion = new Funcion(new Pelicula("P1", "Dune", "Ciencia ficción", 150, "PG-13"), new Sala("Sala 1", 3, 4), ahora.plusHours(30), 16000);
        var palomitas = new Producto("PR1", "Palomitas", 9000); var gaseosa = new Producto("PR2", "Gaseosa", 6000);
        var combo = new Combo("Combo Duo", 10).agregar(palomitas).agregar(gaseosa);
        System.out.println(combo.describir("")+"\nPrecio combo: "+combo.precio());

        System.out.println("\n1) Recargas (solo admin)");
        intentar("Ana intenta recargarse", () -> tarjetas.recargar(ana, ana, 100000));
        intentar("Admin recarga a Ana y Luis", () -> { tarjetas.recargar(admin, ana, 200000); tarjetas.recargar(admin, luis, 200000); });

        System.out.println("\n2) Asiento duplicado");
        var c1 = Compra.builder(ana).entrada(funcion, new Asiento('A', 1)).entrada(funcion, new Asiento('A', 2)).item(combo, 1).build();
        intentar("Ana paga A1,A2 + combo (total " + c1.total() + ")", () -> compras.pagar(c1, ana));
        var c2 = Compra.builder(luis).entrada(funcion, new Asiento('A', 1)).build();
        intentar("Luis intenta A1", () -> compras.pagar(c2, luis));
        System.out.println("  Saldo Luis (debe seguir 200000): " + luis.tarjeta().saldo());
        System.out.println("  Puntos de Ana: " + ana.puntos().disponibles(ahora.toLocalDate()));

        System.out.println("\n3) Cancelación y reembolso único (función en 30 h => 100 %)");
        intentar("Ana cancela", () -> cancel.cancelarCompra(c1, ana));
        intentar("Ana cancela de nuevo", () -> cancel.cancelarCompra(c1, ana));
        System.out.println("  Saldo Ana (debe ser 200000): " + ana.tarjeta().saldo() + " | puntos: " + ana.puntos().disponibles(ahora.toLocalDate()));
        ana.tarjeta().movimientos().forEach(m -> System.out.println("   " + m.tipo() + " " + m.monto() + " saldo " + m.saldoPosterior()));

        System.out.println("\n4) Puntos FEFO y vencimiento");
        var hoy = ahora.toLocalDate();
        luis.puntos().acumular(30, "VIEJO", hoy.minusYears(2));     // vencido
        luis.puntos().acumular(100, "A", hoy.minusMonths(2));       // vence después
        luis.puntos().acumular(50, "B", hoy.minusMonths(11));       // vence primero
        System.out.println("  Válidos: " + luis.puntos().disponibles(hoy) + " (los 30 vencidos no cuentan)");
        var c3 = Compra.builder(luis).item(palomitas, 1).pagarConPuntos().build();   // 9000/20 = 450 pts -> insuficiente
        intentar("Luis paga 450 pts teniendo 150", () -> compras.pagar(c3, luis));
        var gas = new Producto("PR3", "Agua", 2400);   // 120 pts
        var c4 = Compra.builder(luis).item(gas, 1).pagarConPuntos().build();
        intentar("Luis paga 120 pts (FEFO: 50 de B y 70 de A)", () -> compras.pagar(c4, luis));
        luis.puntos().lotes().forEach(l -> System.out.println("   " + l));

        System.out.println("\n5) Cancelación de función por el cine");
        var c5 = Compra.builder(luis).entrada(funcion, new Asiento('B', 1)).build();
        compras.pagar(c5, luis);
        intentar("Cliente intenta cancelar función", () -> cancel.cancelarFuncion(luis, funcion));
        System.out.println("  Compras reembolsadas: " + cancel.cancelarFuncion(admin, funcion) + " | saldo Luis: " + luis.tarjeta().saldo() + " (200000)");
    }
}
