package cinemauq.modelo;
import java.time.*;
/** PATRÓN SINGLETON: configuración y reloj únicos y compartidos (el reloj es reemplazable para pruebas/demo). */
public final class ConfiguracionCine {
    private static final ConfiguracionCine INSTANCIA = new ConfiguracionCine();
    /** Decisión: 1 punto por cada $1.000 COP pagados en dinero. */
    public static final int COP_POR_PUNTO = 1000;
    /** Decisión: al redimir, 1 punto equivale a $20 COP (retorno ~2 %). */
    public static final int VALOR_PUNTO_REDENCION = 20;
    public static final int VIGENCIA_PUNTOS_ANIOS = 1;
    private Clock reloj = Clock.systemDefaultZone();
    private ConfiguracionCine() {}
    public static ConfiguracionCine getInstance() { return INSTANCIA; }
    public LocalDateTime ahora() { return LocalDateTime.now(reloj); }
    public void fijarReloj(Clock c) { this.reloj = c; }
}
