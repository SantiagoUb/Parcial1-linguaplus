package co.edu.uniquindio.poo.parcial1linguaplus.model.material;

public class MaterialVirtualFactory implements MaterialAcademicoFactory{

    @Override
    public MaterialFormativo crearMaterialFormativo(String programa) {
        return new LicenciaPlataforma(programa);
    }

    @Override
    public Carne crearCarne(String estudiante) {
        return new CarneDigital(estudiante);
    }
}
