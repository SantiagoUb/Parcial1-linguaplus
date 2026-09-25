package co.edu.uniquindio.poo.parcial1linguaplus.model.material;

public class MaterialImpreso implements MaterialFormativo {
    private final String programa;

    public MaterialImpreso(String programa) {
        this.programa = programa;
    }

    @Override
    public String describir() {
        return "material impreso para el programa " + programa;
    }
}
