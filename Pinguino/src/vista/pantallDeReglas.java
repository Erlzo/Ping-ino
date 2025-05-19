package vista;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class pantallDeReglas {

    @FXML
    private Label eventos;

    @FXML
    public void initialize() {
        eventos.setText(
           " 1. Cada jugador tira un dado en su turno." +
            " 2. Si el número es par, puede avanzar. " +
            " 3. Si es impar, pierde el turno. " +
            " 4. El primero en llegar al final gana." +
            " ¡Buena suerte! "
        );
    }
}
