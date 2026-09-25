package co.edu.uniquindio.poo.parcial1linguaplus.model.material;

public class CarneFisico implements Carne{
    private final String estudiante;

    public CarneFisico(String estudiante) {
        this.estudiante = estudiante;
    }

    @Override
    public String describri() {
        return "carne fisico para estudiante: " + estudiante;
    }
}
