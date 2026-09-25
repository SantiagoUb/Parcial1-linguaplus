package co.edu.uniquindio.poo.parcial1linguaplus.model.material;

public class CarneDigital implements Carne{
    private final String estudiante;

    public CarneDigital(String estudiante) {
        this.estudiante = estudiante;
    }

    @Override
    public String describri() {
        return "Carne digital generado para estudiante: " + estudiante;
    }
}
