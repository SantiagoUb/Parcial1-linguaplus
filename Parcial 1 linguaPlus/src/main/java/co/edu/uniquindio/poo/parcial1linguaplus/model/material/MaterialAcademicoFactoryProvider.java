package co.edu.uniquindio.poo.parcial1linguaplus.model.material;

import co.edu.uniquindio.poo.parcial1linguaplus.model.Modalidad;

public class MaterialAcademicoFactoryProvider {
    private MaterialAcademicoFactoryProvider() {}

    public static MaterialAcademicoFactory obtenerFactory(Modalidad modalidad) {
        switch (modalidad) {
            case VIRTUAL: return new MaterialVirtualFactory();
            case PRESENCIAL: return new MaterialPresencialFactory();
            default: throw new IllegalArgumentException("No se puede obtener una material academico");
        }
    }
}
