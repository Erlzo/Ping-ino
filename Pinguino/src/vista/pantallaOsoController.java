package vista;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class pantallaOsoController {

    private Stage stage;
    private boolean decision;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public boolean decisionJugador() {
        return decision;
    }

    @FXML
    private void usarPez() {
        
    	
    	
    	decision = true;
        stage.close();
    }

    @FXML
    private void noUsarPez() {
        decision = false;
        stage.close();
    }
}
