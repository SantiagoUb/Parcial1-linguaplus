package cinemauq.reporte;
import cinemauq.persistencia.DatosCine;
import java.time.LocalDate;
/** Producto de la fábrica: una consulta de negocio que devuelve un Reporte. */
public interface ReporteCine { Reporte generar(DatosCine datos, LocalDate desde, LocalDate hasta); }
