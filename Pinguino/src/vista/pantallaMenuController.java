package vista;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

public class pantallaMenuController {

    @FXML
    private void handleNuevaPartida(ActionEvent event) {
        showAlert("Nueva Partida", "Aquí iría la lógica para empezar una nueva partida.");
    }

    @FXML
    private void handleCargarPartida(ActionEvent event) {
        showAlert("Cargar Partida", "Aquí iría la lógica para cargar una partida.");
    }

    @FXML
    private void handleSalir(ActionEvent event) {
        // Cerrar la ventana actual
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
