package src;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class pantallDeReglas {

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
	private void mostrarReglas(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/pantallaReglas.fxml"));
			Parent root = loader.load();

			pantallaOsoController controller = loader.getController();

			Stage popupStage = new Stage();
			controller.setStage(popupStage);

			popupStage.setScene(new Scene(root));
			popupStage.setTitle("Reglas del juego");
			popupStage.initModality(Modality.APPLICATION_MODAL); // bloquea hasta que se cierre
			popupStage.showAndWait();


		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
