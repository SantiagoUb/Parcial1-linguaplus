module co.edu.uniquindio.poo.parcial1linguaplus {
    requires javafx.controls;
    requires javafx.fxml;


    opens co.edu.uniquindio.poo.parcial1linguaplus to javafx.fxml;
    opens co.edu.uniquindio.poo.parcial1linguaplus.controller to javafx.fxml;

    exports co.edu.uniquindio.poo.parcial1linguaplus;
    exports co.edu.uniquindio.poo.parcial1linguaplus.controller;
}