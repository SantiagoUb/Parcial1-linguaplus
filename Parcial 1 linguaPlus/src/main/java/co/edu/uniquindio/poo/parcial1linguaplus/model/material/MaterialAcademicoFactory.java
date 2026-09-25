package co.edu.uniquindio.poo.parcial1linguaplus.model.material;

/*
 * Abstract Factory: garantiza que el material formativo y el carne sean
 * consistentes entre si segun la modalidad. No se puede mezclar material
 * impreso con carne digital, ni licencia de plataforma con carne fisico.
 */
public interface MaterialAcademicoFactory {

    MaterialFormativo crearMaterialFormativo(String programa);
    Carne crearCarne(String estudiante);
}
