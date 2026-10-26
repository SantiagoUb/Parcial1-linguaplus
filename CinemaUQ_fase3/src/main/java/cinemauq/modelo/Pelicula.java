package cinemauq.modelo;
public record Pelicula(String id, String titulo, String genero, int duracionMin, String clasificacion) implements java.io.Serializable {
    public Pelicula {
        if (titulo == null || titulo.isBlank() || duracionMin <= 0) throw new cinemauq.excepcion.ValidacionException("Película: título y duración > 0 obligatorios");
    }
}
