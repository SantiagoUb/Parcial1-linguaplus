package co.edu.uniquindio.poo.parcial1linguaplus.model.material;

public class LicenciaPlataforma implements MaterialFormativo{
    private final String programa;

    public LicenciaPlataforma(String programa) {
        this.programa = programa;
    }

    @Override
    public String describir() {
        return "Licencia para acceso a la plataforma virtual para el programa " + programa;
    }
}
