package cinemauq.persistencia;
import cinemauq.excepcion.CinemaException;
import cinemauq.modelo.*;
import cinemauq.servicio.*;
import java.time.*;
import java.util.*;
/** Datos de demostración deterministas (Random(42)). Se generan con el reloj simulado en fechas pasadas. */
public final class DatosDemo {
    private DatosCine d; private ServicioCompra sc; private ServicioCancelacion canc; private ServicioTarjetas tar;
    private final Random rnd = new Random(42); private final ConfiguracionCine cfg = ConfiguracionCine.getInstance();
    private record Evento(LocalDateTime t, Runnable r) {}
    private final List<Evento> eventos = new ArrayList<>();

    public static DatosCine crear() { return new DatosDemo().construir(); }

    private void en(LocalDateTime t) { var z = ZoneId.systemDefault(); cfg.fijarReloj(Clock.fixed(t.atZone(z).toInstant(), z)); }

    private DatosCine construir() {
        d = new DatosCine(); sc = new ServicioCompra(d.compras); canc = new ServicioCancelacion(d.compras, sc); tar = new ServicioTarjetas();
        var real = LocalDateTime.now(); var hoy = real.toLocalDate();
        var admin = new Administrador("Administrador CinemaUQ", "admin@cinemauq.co", "admin123"); d.admins.add(admin);
        String[] nombres = {"Ana Gómez", "Luis Pérez", "Marta Ríos", "Carlos Díaz", "Sofía Vega"};
        for (int i = 0; i < nombres.length; i++) d.clientes.add(new Cliente(nombres[i], "cliente" + (i + 1) + "@cinemauq.co", "clave123"));
        d.salas.add(new Sala("Sala 1", 5, 8)); d.salas.add(new Sala("Sala 2", 4, 6)); d.salas.add(new Sala("Sala 3 VIP", 6, 10));
        String[][] pel = {{"Dune: Parte Dos", "Ciencia ficción", "166", "PG-13"}, {"Intensamente 2", "Animación", "96", "TP"},
                {"Oppenheimer", "Drama", "180", "R"}, {"Coco", "Animación", "105", "TP"}, {"Mad Max: Furia", "Acción", "148", "R"}};
        for (String[] p : pel) d.peliculas.add(new Pelicula(Ids.nuevo("PEL"), p[0], p[1], Integer.parseInt(p[2]), p[3]));
        var palomitas = new Producto(Ids.nuevo("PRD"), "Palomitas", 9000); var gaseosa = new Producto(Ids.nuevo("PRD"), "Gaseosa", 6000);
        var nachos = new Producto(Ids.nuevo("PRD"), "Nachos", 11000); var perro = new Producto(Ids.nuevo("PRD"), "Perro caliente", 10000);
        var choco = new Producto(Ids.nuevo("PRD"), "Chocolatina", 4500); var agua = new Producto(Ids.nuevo("PRD"), "Agua", 3000);
        d.productos.addAll(List.of(palomitas, gaseosa, nachos, perro, choco, agua));
        var duo = new Combo("Combo Dúo", 10).agregar(palomitas).agregar(gaseosa).agregar(gaseosa);
        var fam = new Combo("Combo Familiar", 15).agregar(nachos).agregar(palomitas).agregar(perro).agregar(gaseosa).agregar(gaseosa);
        d.combos.addAll(List.of(duo, fam));
        var bienvenida = new Promocion("BIENVENIDA", "10 % de bienvenida", 10, hoy.minusYears(1), hoy.plusMonths(6));
        d.promociones.add(bienvenida); d.promociones.add(new Promocion("VERANO20", "20 % verano (vencida)", 20, hoy.minusYears(1), hoy.minusMonths(3)));
        int[] offs = {-40, -35, -30, -25, -20, -16, -12, -9, -6, -4, -3, -2, -1, 1, 2, 3, 5, 7}; long[] precios = {14000, 16000, 18000};
        for (int off : offs) for (int h : new int[]{15, 20})
            d.funciones.add(new Funcion(d.peliculas.get(rnd.nextInt(5)), d.salas.get(rnd.nextInt(3)), hoy.plusDays(off).atTime(h, 0), precios[rnd.nextInt(3)]));

        // lotes históricos (uno ya vencido, otros con vencimientos distintos)
        var c0 = d.clientes.get(0);
        c0.puntos().acumular(40, "HIST-1", hoy.minusDays(400)); c0.puntos().acumular(60, "HIST-2", hoy.minusDays(300)); c0.puntos().acumular(80, "HIST-3", hoy.minusDays(200));

        for (var c : d.clientes) eventos.add(new Evento(real.minusDays(60), () -> tar.recargar(admin, c, 400000)));
        for (int i = 0; i < 12; i++) { var c = d.clientes.get(rnd.nextInt(5)); eventos.add(new Evento(real.minusDays(5 + rnd.nextInt(30)), () -> tar.recargar(admin, c, 150000))); }
        for (int i = 0; i < 55; i++) programarCompra(real, hoy, bienvenida, duo, fam, palomitas, nachos, perro, choco, agua);
        eventos.sort(Comparator.comparing(Evento::t));
        for (var e : eventos) { en(e.t()); try { e.r().run(); } catch (CinemaException ex) { /* intento rechazado: se omite */ } }
        cfg.fijarReloj(Clock.systemDefaultZone());

        // el cine cancela una función futura que ya tenga compras
        d.funciones.stream().filter(f -> f.inicio().isAfter(real.plusDays(2)) && f.ocupados() > 0).findFirst().ifPresent(f -> canc.cancelarFuncion(admin, f));
        return d;
    }

    private void programarCompra(LocalDateTime real, LocalDate hoy, Promocion promo, Combo duo, Combo fam, Producto palomitas, Producto nachos, Producto perro, Producto choco, Producto agua) {
        var fn = d.funciones.get(rnd.nextInt(d.funciones.size())); var cl = d.clientes.get(rnd.nextInt(5));
        var t = fn.inicio().minusHours(2 + rnd.nextInt(70));
        if (t.isAfter(real.minusHours(1))) t = real.minusHours(1 + rnd.nextInt(5));
        boolean conPuntos = rnd.nextInt(5) == 0, soloConfiteria = conPuntos || rnd.nextInt(10) == 0, conPromo = rnd.nextInt(5) == 0, cancelar = rnd.nextInt(7) == 0;
        int nAsientos = 1 + rnd.nextInt(4); int tipoItem = conPuntos ? 5 : rnd.nextInt(6);
        var asientos = new ArrayList<>(fn.sala().asientos()); Collections.shuffle(asientos, rnd);
        final var tf = t;
        eventos.add(new Evento(t, () -> {
            var b = Compra.builder(cl);
            if (!soloConfiteria) asientos.stream().limit(nAsientos).forEach(a -> b.entrada(fn, a));
            switch (tipoItem) {
                case 0 -> b.item(duo, 1); case 1 -> b.item(fam, 1); case 2 -> b.item(new TamanoGrande(palomitas), 1);
                case 3 -> b.item(new ConExtra(nachos, "queso extra", 2500), 1); case 4 -> b.item(perro, 1); default -> b.item(conPuntos ? agua : (soloConfiteria ? choco : agua), conPuntos ? 1 : 2);
            }
            if (conPuntos) b.pagarConPuntos(); else if (conPromo) b.promocion(promo);
            var compra = b.build();
            if (conPuntos && cl.puntos().disponibles(cfg.ahora().toLocalDate()) < compra.costoPuntos()) return;
            sc.pagar(compra, cl);
            if (cancelar) { en(tf.plusHours(1)); canc.cancelarCompra(compra, cl); }
        }));
    }
}
