package cinemauq.vista;
import javafx.scene.Parent;
/** Una pestaña de la aplicación: construye su vista y sabe refrescarse. */
public interface Pantalla { String titulo(); Parent vista(); default void refrescar() {} }
