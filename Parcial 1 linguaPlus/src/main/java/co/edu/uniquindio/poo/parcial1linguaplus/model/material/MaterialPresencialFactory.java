package co.edu.uniquindio.poo.parcial1linguaplus.model.material;

public class MaterialPresencialFactory implements MaterialAcademicoFactory{
    @Override
    public MaterialFormativo crearMaterialFormativo(String programa) {
        return new MaterialImpreso(programa);
    }

    @Override
    public Carne crearCarne(String estudiante) {
        return new CarneFisico(estudiante);
    }
}
