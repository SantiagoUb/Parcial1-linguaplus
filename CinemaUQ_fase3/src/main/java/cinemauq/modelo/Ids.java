package cinemauq.modelo;
import java.util.UUID;
/** Generador de identificadores únicos (compras, entradas, movimientos...). */
public final class Ids {
    private Ids() {}
    public static String nuevo(String prefijo) {
        return prefijo + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
